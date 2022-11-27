package com.comeeatme.batch.processor;

import com.comeeatme.batch.domain.*;
import com.comeeatme.batch.domain.repository.AddressCodeRepository;
import com.comeeatme.batch.domain.repository.JusoLogRepository;
import com.comeeatme.batch.exception.NoRequiredDataException;
import com.comeeatme.batch.service.JusoService;
import com.comeeatme.batch.service.dto.JusoAddressDto;
import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
import lombok.Value;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.IntStream;

public class LocalDataRestaurantEntityBuildProcessor implements ItemProcessor<LocalDataRestaurantDto, LocalData> {

    private static final int COUNT_THRESHOLD = 10;

    private final AddressCodeRepository addressCodeRepository;

    private final JusoService jusoService;

    private final JusoLogRepository jusoLogRepository;

    private final DateTimeFormatter updateDtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    public LocalDataRestaurantEntityBuildProcessor(
            AddressCodeRepository addressCodeRepository, JusoService jusoService, JusoLogRepository jusoLogRepository) {
        this.addressCodeRepository = addressCodeRepository;
        this.jusoService = jusoService;
        this.jusoLogRepository = jusoLogRepository;
    }

    @Override
    public LocalData process(LocalDataRestaurantDto item) throws Exception {
        AddressCode addressCode = getAddressCode(item);
        Address address = Address.builder()
                .name(item.getSiteWhlAddr())
                .roadName(item.getRdnWhlAddr())
                .addressCode(addressCode)
                .build();
        Restaurant restaurant = Restaurant.builder()
                .name(item.getBplcNm())
                .phone(item.getSiteTel())
                .address(address)
                .build();
        return LocalData.builder()
                .managementNum(item.getMgtNo())
                .restaurant(restaurant)
                .serviceId(item.getOpnSvcId())
                .name(item.getOpnSvcNm())
                .category(item.getUptaeNm())
                .permissionDate(item.getApvPermYmd())
                .closedDate(Optional.ofNullable(item.getDcbYmd()).orElse(""))
                .updateAt(LocalDateTime.parse(item.getUpdateDt(), updateDtFormatter))
                .build();
    }

    private AddressCode getAddressCode(LocalDataRestaurantDto item) {
        String siteWhlAddr = item.getSiteWhlAddr();
        if (!StringUtils.hasText(siteWhlAddr)) {
            if (!StringUtils.hasText(item.getRdnWhlAddr())) {
                throw new NoRequiredDataException("지번주소와 도로명주소 모두 없습니다. item=" + item);
            }
            JusoWithKeyword jusoWithKeyword = getJusoAddress(item.getRdnWhlAddr());
            JusoAddressDto.Juso jusoAddress = jusoWithKeyword.getJuso();
            jusoLogRepository.save(
                    JusoLog.addr()
                            .keyword(jusoWithKeyword.keyword)
                            .result(jusoAddress.toString())
                            .localDataManagementNum(item.getMgtNo())
                            .build()
            );
            siteWhlAddr = jusoAddress.getJibunAddr();
        }
        return getAddressCodeBySiteAddress(siteWhlAddr);
    }

    private AddressCode getAddressCodeBySiteAddress(String siteAddress) {
        String param = String.valueOf(siteAddress);
        while (param.contains(" ")) {
            Optional<AddressCode> addressCode = addressCodeRepository.findByFullNameAndTerminalIsTrue(param);
            if (addressCode.isEmpty()) {
                param = subAddress(param);
                continue;
            }
            return addressCode.get();
        }
        throw new NoRequiredDataException("법정동 코드 조회 실패. 주소=" + siteAddress);
    }

    private JusoWithKeyword getJusoAddress(String address) {
        address = String.valueOf(address);
        while (address.contains(" ")) {
            JusoAddressDto jusoAddressDto = jusoService.searchAddress(address);
            if (CollectionUtils.isEmpty(jusoAddressDto.getJuso())) {
                address = subAddress(address);
                continue;
            }
            if (jusoAddressDto.getJuso().size() <= COUNT_THRESHOLD) {
                JusoAddressDto.Juso juso = jusoAddressDto.getJuso().get(0);
                return new JusoWithKeyword(juso, address);
            }
        }
        throw new NoRequiredDataException("검색에 실패했습니다. address=" + address);
    }

    private String subAddress(String address) {
        int idx = IntStream.of(
                address.lastIndexOf(","),
                address.lastIndexOf(" "),
                address.lastIndexOf("(")
        ).max().getAsInt();
        return address.substring(0, idx).trim();
    }

    @Value
    private static class JusoWithKeyword {
        JusoAddressDto.Juso juso;
        String keyword;
    }

}
