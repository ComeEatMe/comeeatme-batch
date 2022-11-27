package com.comeeatme.batch.domain;

import com.comeeatme.batch.domain.core.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Entity
@Table(name = "local_data",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_local_data_restaurant", columnNames = "restaurant_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalData extends BaseTimeEntity implements Persistable<String> {

    // 관리번호
    @Id
    @Column(name = "management_num", length = 45, nullable = false)
    private String managementNum;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;

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
    @Column(name = "permission_date", length = 25, nullable = false)
    private String permissionDate;

    // 폐업 일자
    @Column(name = "closed_date", length = 25, nullable = false)
    private String closedDate;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    private LocalData(
            String managementNum,
            Restaurant restaurant,
            String serviceId,
            String name,
            String category,
            String permissionDate,
            String closedDate,
            LocalDateTime updateAt) {
        this.restaurant = restaurant;
        this.serviceId = serviceId;
        this.name = name;
        this.category = category;
        this.managementNum = managementNum;
        this.permissionDate = permissionDate;
        this.closedDate = closedDate;
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "LocalData{" +
                "managementNum='" + managementNum + '\'' +
                ", restaurant=" + restaurant +
                ", serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", permissionDate='" + permissionDate + '\'' +
                ", closedDate='" + closedDate + '\'' +
                ", localDataLastModifiedAt=" + updateAt +
                '}';
    }

    @Override
    public String getId() {
        return getManagementNum();
    }

    @Override
    public boolean isNew() {
        return isNull(getCreatedAt());
    }

}
