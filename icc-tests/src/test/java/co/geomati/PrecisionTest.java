package co.geomati;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PrecisionTest {

    static final String SOURCE_CRS = "23031";
    static final String TARGET_CRS = "25831";
    static final String ED50_SHP = "/BT5M_289-126/punts_ED50.shp";
    static final String ETRS89_SHP = "/BT5M_289-126/punts_ETRS89.shp";
    static final String[] TRANSFORMS = {"noshift", "7params", "grid", "grid_ign", "similarity"};

    CoordinateReferenceSystem sourceCRS;
    CoordinateReferenceSystem targetCRS;
    SimpleFeatureSource srcFeatures;
    SimpleFeatureSource tgtFeatures;
    
    @Before
    public void setUp() throws Exception {
        CRSAuthorityFactory fact = ReferencingFactoryFinder.getCRSAuthorityFactory("epsg", null);
        sourceCRS = fact.createCoordinateReferenceSystem(SOURCE_CRS);
        targetCRS = fact.createCoordinateReferenceSystem(TARGET_CRS);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() throws Exception {
        TestWKTOperationFactory factory;
        for(int i=0;i<TRANSFORMS.length;i++) { // Test for each transform
            CRS.reset("all");

            String definitions = "/operations/"+TRANSFORMS[i]+".properties";
            factory = new TestWKTOperationFactory(definitions);
            ReferencingFactoryFinder.addAuthorityFactory(factory);
            
            srcFeatures = loadShp(getClass().getResource(ED50_SHP));
            tgtFeatures = loadShp(getClass().getResource(ETRS89_SHP));
            measureTransformPrecision(CRS.findMathTransform(sourceCRS, targetCRS));
            ReferencingFactoryFinder.removeAuthorityFactory(factory);
        }
    }
    
    private double measureTransformPrecision(MathTransform mt) throws Exception {
        System.out.println("Applying transform: " + mt.toWKT());
        SimpleFeatureIterator iterator = srcFeatures.getFeatures().features();
        Point srcGeom, tgtGeom, refGeom;
        FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        int numFeatures = srcFeatures.getFeatures().size();
        double distance[] = new double[numFeatures];
        double sum=0;
        int i=0;
        while (iterator.hasNext()) {
            SimpleFeature srcFeature = iterator.next();
            String id = srcFeature.getID().replace("ED50", "ETRS89");
            Filter filter = ff.id(Collections.singleton(ff.featureId(id)));
            
            srcGeom = (Point)srcFeature.getDefaultGeometry();
            tgtGeom = (Point)JTS.transform(srcGeom, mt);
            SimpleFeatureIterator sfi;
            try {
                sfi = tgtFeatures.getFeatures(filter).features();
                refGeom = (Point)sfi.next().getDefaultGeometry();
                //System.out.println("Transforming " +i);
                distance[i] = refGeom.distance(tgtGeom);
                sum += distance[i++];
                sfi.close();
            } catch (NullPointerException e) {
                System.out.println("!! NOT Transforming " +i);
                i++;
                continue;
            }

            
        }
        double avg = (double)sum/distance.length;
        sum = 0;
        for (i=0; i<distance.length; i++) {
            sum += Math.pow((distance[i] - avg), 2);
        }
        double stddev = Math.sqrt(sum/distance.length);
        System.out.println("    Transformed "+i+" geometries averaging "+ avg + " ± " + stddev + " m");
        iterator.close();
        
        return avg;
    }
    
    private SimpleFeatureSource loadShp(URL url) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        return store.getFeatureSource();
    }

}
