package com.comeeatme.batch.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "address_name", nullable = false)
    private String name;

    @Column(name = "road_address_name", nullable = false)
    private String roadName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_code")
    private AddressCode addressCode;

    @Builder
    private Address(
            String name,
            String roadName,
            AddressCode addressCode
    ) {
        this.name = name;
        this.roadName = roadName;
        this.addressCode = addressCode;
    }

}
