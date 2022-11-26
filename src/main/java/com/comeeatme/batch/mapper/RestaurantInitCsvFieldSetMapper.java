package com.comeeatme.batch.mapper;

import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class RestaurantInitCsvFieldSetMapper implements FieldSetMapper<LocalDataRestaurantDto> {

    @Override
    public LocalDataRestaurantDto mapFieldSet(FieldSet fieldSet) throws BindException {
        LocalDataRestaurantDto restaurant = new LocalDataRestaurantDto();
        restaurant.setRowNum(fieldSet.readString("번호"));
        restaurant.setOpnSfTeamCode(fieldSet.readString("개방자치단체코드"));
        restaurant.setMgtNo(fieldSet.readString("관리번호"));
        restaurant.setOpnSvcId(fieldSet.readString("개방서비스아이디"));
        restaurant.setUpdateGbn(fieldSet.readString("데이터갱신구분"));
        restaurant.setUpdateDt(fieldSet.readString("데이터갱신일자"));
        restaurant.setOpnSvcNm(fieldSet.readString("개방서비스명"));
        restaurant.setBplcNm(fieldSet.readString("사업장명"));
        restaurant.setSitePostNo(fieldSet.readString("소재지우편번호"));
        restaurant.setSiteWhlAddr(fieldSet.readString("소재지전체주소"));
        restaurant.setRdnPostNo(fieldSet.readString("도로명우편번호"));
        restaurant.setRdnWhlAddr(fieldSet.readString("도로명전체주소"));
        restaurant.setSiteArea(fieldSet.readString("소재지면적"));
        restaurant.setApvPermYmd(fieldSet.readString("인허가일자"));
        restaurant.setDcbYmd(fieldSet.readString("인허가취소일자"));
        restaurant.setDtlStateGbn(fieldSet.readString("상세영업상태코드"));
        restaurant.setDtlStateNm(fieldSet.readString("상세영업상태명"));
        restaurant.setX(fieldSet.readString("좌표정보(x)"));
        restaurant.setY(fieldSet.readString("좌표정보(y)"));
        restaurant.setLastModTs(fieldSet.readString("최종수정시점"));
        restaurant.setUptaeNm(fieldSet.readString("업태구분명"));
        restaurant.setSiteTel(fieldSet.readString("소재지전화"));
        return restaurant;
    }

}
