/*
 * Copyright (c) 2012, Oscar Fonts.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  . Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *    
 *  . Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *    
 *  . Neither the name of geomati.co nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package co.geomati;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

public class PerformanceTest {
    
    static final String SOURCE_CRS = "23031";
    static final String TARGET_CRS = "25831";
    static final String ED50_SHP = "/BT5M_289-126/ED50.shp";
    static final String ETRS89_SHP = "/BT5M_289-126/ETRS89.shp";
    static final int ITERATIONS = 6;
    static final String[] TRANSFORMS = {"identity", "similarity", "grid", "7params"};
    
    CoordinateReferenceSystem sourceCRS;
    CoordinateReferenceSystem targetCRS;
    SimpleFeatureCollection featureCollection;

    @Before
    public void setUp() throws Exception {
        CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
        sourceCRS = fact.createCoordinateReferenceSystem(SOURCE_CRS);
        targetCRS = fact.createCoordinateReferenceSystem(TARGET_CRS);        
        featureCollection = loadShp(getClass().getResource(ED50_SHP));
    }
    
    @Test
    public void test() throws Exception {
        // TODO test custom TRANSFORMS
        MathTransform mt = CRS.findMathTransform(sourceCRS, targetCRS);
        iterate(mt);
    }
    
    private SimpleFeatureCollection loadShp(URL url) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        return featureSource.getFeatures();
    }
    
    private void iterate(MathTransform mt) throws TransformException {
        System.out.println("Applying transform: " + mt.toWKT());
        long time[] = new long[ITERATIONS];
        double sum = 0;
        for (int i=0; i<time.length; i++) {
            time[i] = measureTransformTime(mt);
            sum += time[i];
            //System.out.println("  Iteration "+i+": " + time[i] + " ms ");
        }
        double avg = (double)sum/time.length;
        sum = 0;
        for (int i=0; i<time.length; i++) {
            sum += Math.pow((time[i] - avg), 2);
        }
        double stddev = Math.sqrt(sum /10); 
        System.out.println("Transformation time ("+ITERATIONS+" iterations): " + avg + " ± " + stddev + " ms");        
    }
    
    private long measureTransformTime(MathTransform mt) throws TransformException {
        SimpleFeatureIterator iterator = featureCollection.features();
        Geometry geom;
        long t0 = Calendar.getInstance().getTimeInMillis();
        //int i = 0;
        while (iterator.hasNext()) {
            geom = (Geometry)iterator.next().getDefaultGeometry();
            JTS.transform(geom, mt);
            //i++;
        }
        long t1 = Calendar.getInstance().getTimeInMillis();
        //System.out.println("    Transformed "+i+" geometries");
        iterator.close();
        return t1-t0;
    }
}
