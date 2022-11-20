package com.comeeatme.batch.processor;

import com.comeeatme.batch.domain.AddressCode;
import com.comeeatme.batch.domain.dto.AddressCodeDto;
import org.springframework.batch.item.ItemProcessor;

import java.util.Objects;

public class AddressCodeInitProcessor implements ItemProcessor<AddressCodeDto, AddressCode> {


    @Override
    public AddressCode process(AddressCodeDto item) throws Exception {
        if (!Objects.equals("존재", item.getDeleted())) {
            return null;
        }
        if (isRiCode(item)) {
            return null;
        }

        String address = item.getAddress().replaceAll("\\s+", " ");
        int sidoIdx = address.indexOf(" ");
        String siDo = sidoIdx > 0 ? "" : address;
        String code = item.getCode();
        int depth = Objects.equals(address, siDo) ? 1 : 0;
        return AddressCode.builder()
                .name(siDo)
                .fullName(address)
                .code(code)
                .depth(depth)
                .terminal(true)
                .build();
    }

    private static boolean isRiCode(AddressCodeDto item) {
        return !item.getCode().endsWith("0".repeat(AddressCode.RI_CODE_LEN));
    }

}
