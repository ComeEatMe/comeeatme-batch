package com.comeeatme.batch.processor;

import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
import org.springframework.batch.item.ItemProcessor;

public class RestaurantSkipClosedProcessor implements ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> {

    @Override
    public LocalDataRestaurantDto.LocalDataRestaurantDto process(LocalDataRestaurantDto.LocalDataRestaurantDto item) throws Exception {
        if ("폐업".equals(item.getDtlStateNm())) {
            return null;
        }
        return item;
    }

}
