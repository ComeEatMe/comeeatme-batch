package com.comeeatme.batch.mapper;

import com.comeeatme.batch.domain.dto.RestaurantDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class AddressCodeFileFieldSetMapper implements FieldSetMapper<RestaurantDto> {

    @Override
    public RestaurantDto mapFieldSet(FieldSet fieldSet) throws BindException {
        return null;
    }

}
