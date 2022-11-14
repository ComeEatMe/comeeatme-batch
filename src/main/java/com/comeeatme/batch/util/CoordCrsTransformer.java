package com.comeeatme.batch.util;

import org.locationtech.proj4j.*;

public class CoordCrsTransformer {

    private final CoordinateTransform coordinateTransform;

    public CoordCrsTransformer(String sourceCrsName, String targetCrsName) {
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem sourceCrs = crsFactory.createFromName(sourceCrsName);
        CoordinateReferenceSystem targetCrs = crsFactory.createFromName(targetCrsName);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        this.coordinateTransform = ctFactory.createTransform(sourceCrs, targetCrs);
    }

    public ProjCoordinate transform(double x, double y) {
        ProjCoordinate result = new ProjCoordinate();
        coordinateTransform.transform(new ProjCoordinate(x, y), result);
        return result;
    }

}
