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
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.MathTransformProvider;
import org.geotools.util.NameFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Oscar Fonts
 */
public class GridShiftTransformApp {

    public static void main(String[] args) throws Exception {
               
        listMathTransforms();
        printMathTranformProviders();
        printCoordinateOperationAuthorityFactories();
        printCoordinateOperationFactories();
        printCRSAuthorityFactories();
        
        findTransform("23031", "25831");
        findTransform("25831", "23031");
        
        findTransform("23030", "25830");
        findTransform("25830", "23030");
        
        findTransform("4230", "4258");
        findTransform("4258", "4230");
        
        //double[] srcPts = {2.18969857, 41.77052639};
        double[] srcPts = {41.769413434, 2.188547199};
        //                   −0.001112956, −0.001151371
        double[] dstPts = new double[2];
               
        // Discover transform via CRS pair
        transform("4230", "4258", srcPts, dstPts); // Direct transform
        transform("4258", "4230", dstPts, srcPts); // Inverse transform

        System.out.println(dstPts[0]-srcPts[0]);
        System.out.println(dstPts[1]-srcPts[1]);
        
        srcPts[0] = 432648.873;
        srcPts[1] = 4624697.432;
       
        transform("23031", "25831", srcPts, dstPts); // Direct transform
        transform("23030", "25830", srcPts, dstPts); // Direct transform

        System.out.println(dstPts[0]-432555.085);
        System.out.println(dstPts[1]-4624493.134);

        srcPts[0] = 432555.085;
        srcPts[1] = 4624493.134;
        
        transform("25831", "23031", srcPts, dstPts); // Direct transform
        transform("25830", "23030", srcPts, dstPts); // Direct transform

        System.out.println(dstPts[0]-432648.873);
        System.out.println(dstPts[1]-4624697.432);
        
        new GridShiftTransformApp().transformShapefile();
    }

    private static void printCoordinateOperationAuthorityFactories() {
        FactoryRegistry registry = new FactoryRegistry(CoordinateOperationAuthorityFactory.class);
        final Iterator<CoordinateOperationAuthorityFactory> factories =
                registry.getServiceProviders(CoordinateOperationAuthorityFactory.class, null, null);
        while (factories.hasNext()) {
            CoordinateOperationAuthorityFactory factory = factories.next();
            System.out.println(factory.toString());
        }
    }
    
    private static void printCoordinateOperationFactories() {
        FactoryRegistry registry = new FactoryRegistry(CoordinateOperationFactory.class);
        final Iterator<CoordinateOperationFactory> factories =
                registry.getServiceProviders(CoordinateOperationFactory.class, null, null);
        while (factories.hasNext()) {
            CoordinateOperationFactory factory = factories.next();
            System.out.println(factory.toString());
        }
    }
    
    private static void printCRSAuthorityFactories() {
        FactoryRegistry registry = new FactoryRegistry(CRSAuthorityFactory.class);
        final Iterator<CRSAuthorityFactory> factories =
                registry.getServiceProviders(CRSAuthorityFactory.class, null, null);
        while (factories.hasNext()) {
            CRSAuthorityFactory factory = factories.next();
            System.out.println(factory.toString());
        }
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
    
    private static void findTransform(String source, String target) {
        try {
            CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
            CoordinateReferenceSystem sourceCRS = fact.createCoordinateReferenceSystem(source);
            CoordinateReferenceSystem targetCRS = fact.createCoordinateReferenceSystem(target);
            
            System.out.println(source+"="+sourceCRS.toWKT());
            System.out.println(target+"="+targetCRS.toWKT());
            
            MathTransform mt = CRS.findMathTransform(sourceCRS, targetCRS);
            System.out.println("=>CRS.findMathTransform=>");
            System.out.println(mt.toString());
            
            CoordinateOperationFactory cof = ReferencingFactoryFinder.getCoordinateOperationFactory(null);
            CoordinateOperation op = cof.createOperation(sourceCRS, targetCRS);
                
            System.out.println("** Transform from "+source+" to "+target+":");
            System.out.print("* Operation: ");
            System.out.println(op.toString());
            System.out.println();
            System.out.print("* Math Transform: ");
            System.out.println(op.getMathTransform().toString());
            System.out.println();
            
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
       
    private static void listMathTransforms() {
        Set<OperationMethod> s = ReferencingFactoryFinder.getMathTransformFactory(null).getAvailableMethods(null/*Transformation.class*/);
        Iterator<OperationMethod> i = s.iterator();
        while(i.hasNext()) {
            OperationMethod m = i.next();
            System.out.println("{"+m.getSourceDimensions()+","+m.getTargetDimensions()+"} '" + m.getName().getCode() +"' " + m.getParameters().descriptors().toString());
        }    
    }
    
    protected static String trimAuthority(ReferenceIdentifier id) {
        String code = id.getCode();
        code = code.trim();
        final GenericName name  = NameFactory.create(code);
        final GenericName scope = name.scope().name();
        if (scope == null) {
            return code;
        }
        if (Citations.identifierMatches(id.getAuthority(), scope.toString())) {
            return name.tip().toString().trim();
        }
        return code;
    }
    
    private void transformShapefile() throws IOException, NoSuchAuthorityCodeException, FactoryException, URISyntaxException {       
        CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
        CoordinateReferenceSystem sourceCRS = fact.createCoordinateReferenceSystem("23031");
        CoordinateReferenceSystem targetCRS = fact.createCoordinateReferenceSystem("25831");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        System.out.println("Applying transform: " + transform.toWKT());
        
        URI file_in = new URI("file:///mnt/d/DocumentsOscar/projects/ntv2/data/ICC/bt25mv10sh0f7332c0r011/bt25mv10sh0f7332pl0r011.shp");
        URI file_out = new URI("file:///mnt/d/DocumentsOscar/projects/ntv2/data/ICC/bt25mv10sh0f7332c0r011/out.shp");

        //URI file_in = new URI("file:///mnt/d/DocumentsOscar/projects/ntv2/data/ICC/ed50_etrs89.shp");
        //URI file_out = new URI("file:///mnt/d/DocumentsOscar/projects/ntv2/data/ICC/etrs89_etrs89.shp");
        
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
            int i = 0;
            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                copy.setDefaultGeometry(geometry2);
                writer.write();
                i++;
            }
            transaction.commit();
            long t1 = Calendar.getInstance().getTimeInMillis();
            System.out.println("Shapefile transform ("+String.valueOf(i)+" geometries) took " + String.valueOf(t1-t0) + " ms");
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
