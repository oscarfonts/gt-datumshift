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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.geotools.metadata.iso.citation.Citations;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.Parameter;
import org.geotools.parameter.ParameterGroup;
import org.geotools.referencing.NamedIdentifier;
import org.geotools.referencing.operation.MathTransformProvider;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.Transformation;

import au.com.objectix.jgridshift.GridShift;
import au.com.objectix.jgridshift.GridShiftFile;

/**
 * The provider for the "<cite>NTv2</cite>" coordinate transformation method (EPSG:9615).
 * <p>
 * This transformation depends on an external resource (the NTv2 grid file). If the file
 * is not available, a recoverable {@link NoSuchIdentifierException} will be thrown on
 * instantiation. See {@link IdentifiedObjectSet}.
 *
 * @source $URL$
 * @version $Id$
 * @author Oscar Fonts
 */
public class NTv2Transform extends AbstractMathTransform implements MathTransform2D, Serializable {

    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = -3082112044314062512L;

    /** The grid file name as set in the constructor. */
    private URI fileParam = null;
    
    /** The full path location to the grid file on disk. */
    private URI gridFileLocation = null;
    
    /** The grid data */
    private GridShiftFile grid = null;
    
    /** Holds a coordinate pair and provides access to its shifted values */
    private GridShift shift = null;
    
    /**
     * The inverse of this transform. Will be created only when needed.
     */
    private transient MathTransform2D inverse;
    
    /**
     * Constructs a {@code NTv2Transform} from the specified grid shift file.
     * This constructor checks for grid file existence and format, but
     * doesn't actually load the full grid into memory to preserve resources.
     *
     * @param latGridName path and name (or just name if {@link #GRID_LOCATION}
     *        is set) to the NTv2 grid file.
     * @throws NoSuchIdentifierException
     */
    public NTv2Transform(URI file) throws NoSuchIdentifierException {
        fileParam = file;
        
        // Search grid file
        try {
            gridFileLocation = getClass().getResource("/"+fileParam.toString()).toURI();
        } catch (NullPointerException e) {
            throw new NoSuchIdentifierException("NTv2 Grid File not found.", fileParam.toString());
        } catch (URISyntaxException e) {
            throw new NoSuchIdentifierException("Malformed NTv2 Grid File location.", fileParam.toString());
        }

        // Check grid file format
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(gridFileLocation), "r");
            new GridShiftFile().loadGridShiftFile(raf);
        } catch (IllegalArgumentException e) {
            // This usually means that file has not a valid NTv2 format.
            throw new NoSuchIdentifierException(e.getLocalizedMessage(), fileParam.toString());
        } catch (IOException e) {
            throw new NoSuchIdentifierException("NTv2 Grid File read error.", fileParam.toString());
        } finally {
            try {
                if (raf!=null) raf.close();
            } catch (IOException e) {}
        }
    }
    
    /**
     * Returns the inverse of this transform.
     *
     * @return the inverse of this transform
     */
    @Override
    public synchronized MathTransform2D inverse() {
        if (inverse == null) {
            inverse = new Inverse();
        }
        return inverse;
    }

    /**
     * Read full grid shift file and load it into memory.
     * 
     * Some checking was made at instantiation time to minimize
     * external resource reading exceptions at this stage.
     * 
     * @throws TransformException if error reading file
     */
    private void loadGrid() throws TransformException {
        grid = new GridShiftFile();
        shift = new GridShift();        
        try {
            InputStream in = new FileInputStream(new File(gridFileLocation));
            grid.loadGridShiftFile(in, false);
            in.close();
        } catch (FileNotFoundException e) {
            throw (new TransformException(e.getLocalizedMessage(), e));
        } catch (IOException e) {
            throw (new TransformException(e.getLocalizedMessage(), e));
        }
    }
    
    /**
     * Transforms a list of coordinate point ordinal values. This method is
     * provided for efficiently transforming many points. The supplied array
     * of ordinal values will contain packed ordinal values.  For example, if
     * the source dimension is 3, then the ordinals will be packed in this
     * order:
     * (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
     *
     * <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var>
     * ...).
     *
     * @param srcPts the array containing the source point coordinates.
     * @param srcOff the offset to the first point to be transformed in the
     *        source array.
     * @param dstPts the array into which the transformed point coordinates are
     *        returned. May be the same than {@code srcPts}.
     * @param dstOff the offset to the location of the first transformed point
     *        that is stored in the destination array.
     * @param numPts the number of point objects to be transformed.
     *
     * @throws TransformException if an IO error occurs reading the grid file.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts,
            int dstOff, int numPts) throws TransformException {
        bidirectionalTransform(srcPts,srcOff, dstPts, dstOff, numPts, true);
    }
    
    /**
     * Inverse transform. See {@link #transform(double[], int, double[],
     *       int, int)}
     *
     * @param srcPts the array containing the source point coordinates.
     * @param srcOff the offset to the first point to be transformed in the
     *        source array.
     * @param dstPts the array into which the transformed point coordinates are
     *        returned. May be the same than {@code srcPts}.
     * @param dstOff the offset to the location of the first transformed point
     *        that is stored in the destination array.
     * @param numPts the number of point objects to be transformed.
     *
     * @throws TransformException if an IO error occurs reading the grid file.
     */
    public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts,
            int dstOff, int numPts) throws TransformException {
        bidirectionalTransform(srcPts,srcOff, dstPts, dstOff, numPts, false);
    }

    /**
     * Performs the actual transformation.
     *
     * @param srcPts the array containing the source point coordinates.
     * @param srcOff the offset to the first point to be transformed in the
     *        source array.
     * @param dstPts the array into which the transformed point coordinates are
     *        returned. May be the same than {@code srcPts}.
     * @param dstOff the offset to the location of the first transformed point
     *        that is stored in the destination array.
     * @param numPts the number of point objects to be transformed.
     * @param forward {@code true} for direct transform, {@code false} for inverse transform.
     *
     * @throws TransformException if an IO error occurs reading the grid file.
     */
    private void bidirectionalTransform(double[] srcPts, int srcOff, double[] dstPts,
            int dstOff, int numPts, boolean forward) throws TransformException {

        boolean shifted;
        
        // Load grid only when first needed. Saves memory.
        if (grid == null) loadGrid();
        
        try {
            for (int i=0; i<srcPts.length; i=i+2) {
                shift.setLonPositiveEastDegrees(srcPts[i]);
                shift.setLatDegrees(srcPts[i+1]);
                if (forward) {
                    shifted = grid.gridShiftForward(shift);
                } else {
                    shifted = grid.gridShiftReverse(shift);
                }
                if (shifted) {
                    dstPts[i]=shift.getShiftedLonPositiveEastDegrees();
                    dstPts[i+1]=shift.getShiftedLatDegrees();
                } else {
                    // TODO: Warn! Point out of grid domain, will not be shifted!
                    dstPts[i]=srcPts[i];
                    dstPts[i+1]=srcPts[i+1];                    
                }
            }
        } catch (IOException e) {
            throw new TransformException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public int getSourceDimensions() {
        return 2;
    }

    @Override
    public int getTargetDimensions() {
        return 2;
    }
    
    /**
     * Returns the parameter values for this math transform.
     *
     * @return A copy of the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final ParameterValue<URI> file = new Parameter<URI>(Provider.FILE);
        file.setValue(fileParam);

        return new ParameterGroup(Provider.PARAMETERS,
            new ParameterValue[] { file }
        );
    }
    
    /**
     * Inverse of a {@link NTv2Transform}.
     *
     * @version $Id$
     * @author Oscar Fonts
     */
    private final class Inverse extends AbstractMathTransform.Inverse
            implements MathTransform2D, Serializable
    {
        /** Serial number for interoperability with different versions. */
        private static final long serialVersionUID = -4707304160205218546L;

        /**
         * Default constructor.
         */
        public Inverse() {
            NTv2Transform.this.super();
        }

        /**
         * Returns the parameter values for this math transform.
         *
         * @return A copy of the parameter values for this math transform.
         */
        @Override
        public ParameterValueGroup getParameterValues() {
            return null;
        }

        /**
         * Inverse transform an array of points.
         *
         * @param source
         * @param srcOffset
         * @param dest
         * @param dstOffset
         * @param length
         *
         * @throws TransformException if the input point is outside the area
         *         covered by this grid.
         */
        public void transform(final double[] source, final int srcOffset,
            final double[] dest, final int dstOffset, final int length)
            throws TransformException {
            NTv2Transform.this.inverseTransform(source, srcOffset, dest,
                dstOffset, length);
        }

        /**
         * Returns the original transform.
         */
        @Override
        public MathTransform2D inverse() {
            return (MathTransform2D) super.inverse();
        }

        /**
         * Restore reference to this object after deserialization.
         *
         * @param in DOCUMENT ME!
         * @throws IOException DOCUMENT ME!
         * @throws ClassNotFoundException DOCUMENT ME!
         */
        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            NTv2Transform.this.inverse = this;
        }
    }

    /**
     * 
     * @author Oscar Fonts
     *
     */
    public static class Provider extends MathTransformProvider {
        
        private static final long serialVersionUID = -3710592152744574801L;

        /**
         * The operation parameter descriptor for the "Latitude and longitude difference file"
         * parameter value. The default value is "".
         */
        public static final DefaultParameterDescriptor<URI> FILE = new DefaultParameterDescriptor<URI>(
            toMap(new NamedIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Latitude and longitude difference file"),
                new NamedIdentifier(Citations.EPSG, "8656")
            }),
            URI.class, null, null, null, null, null, true);
        
        /**
         * The parameters group.
         */
        static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "NTv2"),
                new NamedIdentifier(Citations.EPSG, "9615")
            }, new ParameterDescriptor[] {
                FILE
            });
        
        /**
         * Constructs a provider.
         */
        public Provider() {
            super(2, 2, PARAMETERS);
        }
        
        /**
         * Returns the operation type.
         */
        @Override
        public Class<Transformation> getOperationType() {
            return Transformation.class;
        }
        
        /**
         * Creates a math transform from the specified group of parameter
         * values.
         *
         * @param values The group of parameter values.
         * @return The created math transform.
         * @throws ParameterNotFoundException if a required parameter was not
         *         found.
         * @throws FactoryException if there is a problem creating this
         *         math transform.
         */
        protected MathTransform createMathTransform(final ParameterValueGroup values)
                throws ParameterNotFoundException, FactoryException
        {
            return new NTv2Transform(value(FILE, values));
        }
    }
}
