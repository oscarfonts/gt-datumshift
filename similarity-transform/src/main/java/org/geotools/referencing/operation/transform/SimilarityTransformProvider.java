/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.referencing.operation.transform;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.referencing.operation.MathTransformProvider;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
    
/**
 * @author Oscar Fonts
 *
 */
public class SimilarityTransformProvider extends MathTransformProvider {

    private static final long serialVersionUID = -7413519919588731455L;

    // TODO: TRANSLATION_1 and TRANSLATION_2 should be expressed in "target CRS units", not necessarily SI.METER.
    
    public static final ParameterDescriptor<Double> TRANSLATION_1 = createDescriptor(
        new NamedIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Ordinate 1 of evaluation point in target CRS"),
            new NamedIdentifier(Citations.EPSG, "8621")
        },
        0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METER); 

    public static final ParameterDescriptor<Double> TRANSLATION_2 = createDescriptor(
        new NamedIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Ordinate 2 of evaluation point in target CRS"),
            new NamedIdentifier(Citations.EPSG, "8622")
        },
        0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METER);
    
    public static final ParameterDescriptor<Double> SCALE = createDescriptor(
        new NamedIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Scale difference"),
            new NamedIdentifier(Citations.EPSG, "8611")
        },
        1, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Dimensionless.UNIT);
    
    public static final ParameterDescriptor<Double> ROTATION = createDescriptor(
        new NamedIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Rotation angle of source coordinate reference system axes"),
            new NamedIdentifier(Citations.EPSG, "8614")
        },
        0, -180, +180, NonSI.SECOND_ANGLE);

    static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
        new NamedIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Similarity transformation"),
            new NamedIdentifier(Citations.EPSG, "9621")
        },
        new ParameterDescriptor[] {
            TRANSLATION_1,
            TRANSLATION_2,
            SCALE,
            ROTATION
        }
    );
    
    public SimilarityTransformProvider() {
        super(2, 2, PARAMETERS);
    }

    protected MathTransform createMathTransform(ParameterValueGroup values)
            throws InvalidParameterNameException, ParameterNotFoundException,
            InvalidParameterValueException, FactoryException {
        
        // The four parameters
        double t1 = doubleValue(TRANSLATION_1, values);
        double t2 = doubleValue(TRANSLATION_2, values);
        double scale = doubleValue(SCALE, values);
        double rotation = doubleValue(ROTATION, values);
        
        // Calculate affine transform coefficients
        double theta = Math.PI * rotation / 648000; // arcsec to rad
        double p1 = scale * Math.cos(theta);
        double p2 = scale * Math.sin(theta);
        
        return new AffineTransform2D(p1, -p2, p2, p1, t1, t2);
    }

}

