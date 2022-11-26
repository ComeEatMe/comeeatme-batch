package com.comeeatme.batch.domain;

import com.comeeatme.batch.domain.core.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "restaurant",
        indexes = {
        @Index(name = "IX_restaurant_name", columnList = "name")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "phone", length = 25, nullable = false)
    private String phone;

    @Embedded
    private Address address;

    @Builder
    private Restaurant(
            Long id,
            String name,
            String phone,
            Address address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", addressName='" + address.getName() + '\'' +
                ", addressRoadName='" + address.getRoadName() + '\'' +
                ", addressCode='" + Optional.ofNullable(address.getAddressCode())
                    .map(AddressCode::getCode).orElse(null) + '\'' +
                '}';
    }

}
