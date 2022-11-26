package com.comeeatme.batch.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "open_info",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_open_info_management_num", columnNames = "management_num"),
                @UniqueConstraint(name = "UK_open_info_restaurant", columnNames = "restaurant_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_info_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;

    // 관리번호
    @Column(name = "management_num", length = 45, nullable = false)
    private String managementNum;

    // 개방서비스아이디
    @Column(name = "service_id", length = 15, nullable = false)
    private String serviceId;

    // 개방서비스명
    @Column(name = "name", length = 15, nullable = false)
    private String name;

    // 업태구분명
    @Column(name = "category", length = 25, nullable = false)
    private String category;

    // 인허가일자
    @Column(name = "permission_date", nullable = false)
    private LocalDate permissionDate;

    // 최종수정시점
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    // 폐업 일자
    @Column(name = "closed_date")
    private LocalDate closedDate;

    @Builder
    private OpenInfo(
            Long id,
            Restaurant restaurant,
            String serviceId,
            String name,
            String category,
            String managementNum,
            LocalDate permissionDate,
            LocalDateTime lastModifiedAt,
            LocalDate closedDate) {
        this.id = id;
        this.restaurant = restaurant;
        this.serviceId = serviceId;
        this.name = name;
        this.category = category;
        this.managementNum = managementNum;
        this.permissionDate = permissionDate;
        this.lastModifiedAt = lastModifiedAt;
        this.closedDate = closedDate;
    }

    @Override
    public String toString() {
        return "OpenInfo{" +
                "id=" + id +
                ", restaurant=" + restaurant +
                ", managementNum='" + managementNum + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", permissionDate=" + permissionDate +
                ", lastModifiedAt=" + lastModifiedAt +
                ", closedDate=" + closedDate +
                '}';
    }

}
