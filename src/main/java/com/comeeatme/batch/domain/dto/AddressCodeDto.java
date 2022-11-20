package com.comeeatme.batch.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressCodeDto {

    private String code;

    private String address;

    private String deleted;

}
