package com.comeeatme.batch.processor;

import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class RestaurantTrimProcessor implements ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> {

    @Override
    public LocalDataRestaurantDto.LocalDataRestaurantDto process(LocalDataRestaurantDto.LocalDataRestaurantDto item) throws Exception {
        item.setRdnWhlAddr(trimAddr(item.getRdnWhlAddr()));
        item.setSiteWhlAddr(trimAddr(item.getSiteWhlAddr()));
        item.setSiteTel(trimSiteTel(item.getSiteTel()));
        return item;
    }

    private String trimAddr(String addr) {
        if (StringUtils.hasText(addr)) {
            addr = addr.replaceAll("\\s+", " ");
        }
        return addr;
    }

    private String trimSiteTel(String siteTel) {
        if (StringUtils.hasText(siteTel)) {
            siteTel = siteTel
                    .replace("-", "")
                    .replaceAll("\\s+", "");
        }
        return siteTel;
    }

}
