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
package org.geotools;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.FactoryRegistry;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.MathTransformProvider;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Oscar Fonts
 */
public class GridShiftTransformApp {

    public static void main(String[] args) throws Exception {
        
        new GridShiftTransformApp().transformShapefile();
        
        //printMathTranformProviders();
        
        //findTransforms("23030", "25830");
        //findTransforms("4230", "4258");
        /*
        double[] srcPts = {41.77052639, 2.18969857};
        //double[] srcPts = {41.769413434, 2.188547199};
        //                   −0.001112956, −0.001151371
        double[] dstPts = new double[2];

        // Discover transform via CRS pair
        //transform("4230", "4258", srcPts, dstPts); // Direct transform
        //transform("4258", "4230", dstPts, srcPts); // Inverse transform
        
        srcPts[0] = 432648.873;
        srcPts[1] = 4624697.432;
       
        //transform("23031", "25831", srcPts, dstPts); // Direct transform

        System.out.println(dstPts[0]-432555.085);
        System.out.println(dstPts[1]-4624493.134);
        System.out.println();

        srcPts[0] = 432555.085;
        srcPts[1] = 4624493.134;
        
        //transform("25831", "23031", srcPts, dstPts); // Direct transform

        System.out.println(dstPts[0]-432648.873);
        System.out.println(dstPts[1]-4624697.432);
        System.out.println();
        */
    }

    private static void printMathTranformProviders() {
        FactoryRegistry registry = new FactoryRegistry(MathTransformProvider.class);
        MathTransformProvider provider = null;
        final Iterator<MathTransformProvider> providers =
                registry.getServiceProviders(MathTransformProvider.class, null, null);
        while (providers.hasNext()) {
            provider = providers.next();
            System.out.println(provider.toString());
        }
    }
    
    private static void findTransforms(String source, String target) {
        try {
            CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
            CoordinateReferenceSystem sourceCRS = fact.createCoordinateReferenceSystem(source);
            CoordinateReferenceSystem targetCRS = fact.createCoordinateReferenceSystem(target);

            CoordinateOperationAuthorityFactory coaf = ReferencingFactoryFinder.getCoordinateOperationAuthorityFactory("epsg",null);
            Set<CoordinateOperation> set = coaf.createFromCoordinateReferenceSystemCodes(source, target);
            Iterator<CoordinateOperation> it = set.iterator();
            while(it.hasNext()) {
                CoordinateOperation op = it.next();
                System.out.println(op.toString());
                System.out.println();
            }
        } catch (FactoryException e) {
            System.out.println("Factory Exception: " + e.getMessage());
        }
    }
    
    private static void transform(String source, String target, double[] srcPts, double[] dstPts) {
        try {
            CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
            CoordinateReferenceSystem sourceCRS = fact.createCoordinateReferenceSystem(source);
            CoordinateReferenceSystem targetCRS = fact.createCoordinateReferenceSystem(target);
            
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
            transform.transform(srcPts, 0, dstPts, 0, srcPts.length/transform.getSourceDimensions());
            
            System.out.println("Transforming from " + source + " to " + target);
            System.out.println("Applying transform: " + transform.toWKT());
            System.out.println("Source coordinates: " + Arrays.toString(srcPts));
            System.out.println("Target coordinates: " + Arrays.toString(dstPts));
            System.out.println();
        } catch (FactoryException e) {
            System.out.println("Factory Exception: " + e.getMessage());
        } catch (TransformException e) {
            System.out.println("Transform Exception: " + e.getMessage());
        }
    }
    
    private static void transform(String datumShift, double[] srcPts, double[] dstPts) {
        transform(datumShift, srcPts, dstPts, false);
    }
    
    private static void inverseTransform(String datumShift, double[] srcPts, double[] dstPts) {
        transform(datumShift, srcPts, dstPts, true);
    }
    
    private static void transform(String datumShift, double[] srcPts, double[] dstPts, boolean inverse) {
        try {
            CoordinateOperationAuthorityFactory fact = ReferencingFactoryFinder.getCoordinateOperationAuthorityFactory("epsg", null);
            CoordinateOperation operation = fact.createCoordinateOperation(datumShift);
            
            MathTransform transform = operation.getMathTransform();
            
            if (inverse) {
                transform = transform.inverse();
            }
            
            transform.transform(srcPts, 0, dstPts, 0, srcPts.length/transform.getSourceDimensions());
            
            System.out.println("Applying transform: " + transform.toWKT());
            System.out.println("Source coordinates: " + Arrays.toString(srcPts));
            System.out.println("Target coordinates: " + Arrays.toString(dstPts));
            System.out.println();
        } catch (FactoryException e) {
            System.out.println("Factory Exception: " + e.getMessage());
        } catch (TransformException e) {
            System.out.println("Transform Exception: " + e.getMessage());
        }
    }
    
    private void transformShapefile() throws IOException, NoSuchAuthorityCodeException, FactoryException, URISyntaxException {       
        CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
        CoordinateReferenceSystem sourceCRS = fact.createCoordinateReferenceSystem("23031");
        CoordinateReferenceSystem targetCRS = fact.createCoordinateReferenceSystem("25831");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        System.out.println("Applying transform: " + transform.toWKT());
        
        URI file_in = new URI("file:///home/oscar/Escritorio/5m-23031/bt5mv20sh0f289125pl0r050.shp");
        URI file_out = new URI("file:///home/oscar/Escritorio/5m-23031/out.shp");
        
        FileDataStore store = FileDataStoreFinder.getDataStore(file_in.toURL());
        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureType schema = featureSource.getSchema();
       
        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
        Map<String, Serializable> create = new HashMap<String, Serializable>();
        create.put("url", file_out.toURL());
        create.put("create spatial index", Boolean.FALSE);
        create.put("memory mapped buffer", Boolean.TRUE);
        create.put("cache and reuse memory maps", Boolean.TRUE);
        DataStore dataStore = factory.createNewDataStore(create);
        SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, targetCRS);
        dataStore.createSchema(featureType);

        SimpleFeatureCollection featureCollection = featureSource.getFeatures();
        
        Transaction transaction = new DefaultTransaction("Reproject");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                        dataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = featureCollection.features();
        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            long i = 0;
            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                copy.setDefaultGeometry(geometry2);
                writer.write();                                
            }
            transaction.commit();
            long t1 = Calendar.getInstance().getTimeInMillis();
            System.out.println("Shapefile transform :" + String.valueOf(t1-t0));
        } catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();
        } finally {
            writer.close();
            iterator.close();
            transaction.close();
        }
    }
    
}
