package com.comeeatme.batch.domain;

import com.comeeatme.batch.domain.core.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "incomplete_restaurant",
        indexes = {
            @Index(name = "IX_incomplete_restaurant_name", columnList = "name"),
            @Index(name = "IX_incomplete_restaurant_open_info_management_num", columnList = "open_info_management_num")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncompleteRestaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incomplete_restaurant_id")
    private Long id;

    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "phone", length = 25)
    private String phone;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "road_address_name")
    private String addressRoadName;

    // 관리번호
    @Column(name = "open_info_management_num", length = 65)
    private String openInfoManagementNum;

    // 개방서비스아이디
    @Column(name = "open_info_service_id", length = 15)
    private String openInfoServiceId;

    // 개방서비스명
    @Column(name = "open_info_name", length = 15)
    private String openInfoName;

    // 업태구분명
    @Column(name = "open_info_category", length = 15)
    private String openInfoCategory;

    // 인허가일자
    @Column(name = "open_info_permission_date")
    private LocalDate openInfoPermissionDate;

    // 최종수정시점
    @Column(name = "open_info_last_modified_at")
    private LocalDateTime openInfoLastModifiedAt;

    @Builder
    private IncompleteRestaurant(
            Long id,
            String name,
            String phone,
            String addressName,
            String addressRoadName,
            String openInfoManagementNum,
            String openInfoServiceId,
            String openInfoName,
            String openInfoCategory,
            LocalDate openInfoPermissionDate,
            LocalDateTime openInfoLastModifiedAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.addressName = addressName;
        this.addressRoadName = addressRoadName;
        this.openInfoManagementNum = openInfoManagementNum;
        this.openInfoServiceId = openInfoServiceId;
        this.openInfoName = openInfoName;
        this.openInfoCategory = openInfoCategory;
        this.openInfoPermissionDate = openInfoPermissionDate;
        this.openInfoLastModifiedAt = openInfoLastModifiedAt;
    }

}
