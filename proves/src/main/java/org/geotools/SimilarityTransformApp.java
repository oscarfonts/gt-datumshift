package org.geotools;

import java.util.Arrays;
import java.util.Iterator;

import org.geotools.factory.FactoryRegistry;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.MathTransformProvider;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class SimilarityTransformApp 
{    
    public static void main(String[] args)
    {
        
        double[] srcPts = {300000, 4500000};
        //double[] srcPts = {299905.060, 4499796.515};
        double[] dstPts = new double[2];

        // List all available math transform providers
        //printMathTranformProviders();
        
        // Apply specified transform
        //transform("5166", srcPts, dstPts);        // Direct transform
        //inverseTransform("5166", dstPts, srcPts); // Inverse transform

        // Discover transform via CRS pair
        transform("23031", "25831", srcPts, dstPts); // Direct transform
        //transform("25831", "23031", dstPts, srcPts); // Inverse transform
        
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
}
