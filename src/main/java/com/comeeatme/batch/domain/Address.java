package com.comeeatme.batch.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "address_name", nullable = false)
    private String name;

    @Column(name = "road_address_name", nullable = false)
    private String roadName;

    @Column(name = "location",  nullable = false)
    private Point location;

    @Builder
    private Address(
            String name,
            String roadName,
            Double x,
            Double y) {
        this.name = name;
        this.roadName = roadName;
        this.location = createPoint(x, y);
    }

    public static Point createPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory(
                new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

}
