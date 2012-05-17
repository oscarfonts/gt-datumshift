SimilarityTransformProvider
===========================

.. code-block:: java

   package org.geotools.referencing.operation.transform 


Herència::

    Object
      Formattable
          AbstractIdentifiedObject
              DefaultOperationMethod
                  MathTransformProvider
                      SimilarityTransformProvider

Signatura:

.. code-block:: java

    public class SimilarityTransformProviderextends MathTransformProvider

Descripció:

El *provider* per a la "Similarity transformation" (EPSG 9621).

Advertiu que la transformació de semblança és un cas particular d'una transformació afí 2D.

Autor:

Oscar Fonts

Atributs
--------

TRANSLATION\_1
~~~~~~~~~~~~~~

.. code-block:: java

    public static final ParameterDescriptor<Double> TRANSLATION_1

"Ordinate 1 of evaluation point in target CRS" EPSG::8621

TRANSLATION\_2
~~~~~~~~~~~~~~

.. code-block:: java

    public static final ParameterDescriptor<Double> TRANSLATION_2

"Ordinate 2 of evaluation point in target CRS" EPSG::8622

SCALE
~~~~~

.. code-block:: java

    public static final ParameterDescriptor<Double> SCALE

"Scale difference" EPSG::8611

ROTATION
~~~~~~~~

.. code-block:: java

    public static final ParameterDescriptor<Double> ROTATION

"Rotation angle of source coordinate reference system axes" EPSG::8614

Constructors
------------

SimilarityTransformProvider
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public SimilarityTransformProvider()

Crea una transformació de semblança 2D. EPSG defineix explícitament aquesta transformació com a bidimensional.

Mètodes
-------

createMathTransform
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected MathTransform createMathTransform(ParameterValueGroup values) throws ParameterNotFoundException

Construeix una transformació matemàtica ``AffineTransform2D`` amb els valors de paràmetres especificats.
La transformació de semblança és un cas particular d'una transformació afí 2D on::

   m00 = SCALE * cos(ROTATION)
   m01 = SCALE * sin(ROTATION)
   m02 = TRANSLATION_1
   m10 = -m01
   m11 = m00
   m12 = TRANSLATION_2
         

Especificat a:

``createMathTransform`` a la classe ``MathTransformProvider``.

Paràmetres:

``values`` - Els valors per al grup de paràmetres ``PARAMETERS``

Retorn:

Una ``AffineTransform2D``.

Llença:

``ParameterNotFoundException`` - si falta algun dels paràmetres obligatoris.

