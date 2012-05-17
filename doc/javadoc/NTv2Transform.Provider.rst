NTv2Transform.Provider
======================

.. code-block:: java

   package org.geotools.referencing.operation.transform 

Herència::

    Object
      Formattable
          AbstractIdentifiedObject
              DefaultOperationMethod
                  MathTransformProvider
                      NTv2Transform.Provider

Signatura:

.. code-block:: java

    public static class NTv2Transform.Provider extends MathTransformProvider

Descripció:

El proveïdor per a ``NTv2Transform``.

Autor:

Oscar Fonts

Atributs
--------

FILE
~~~~

.. code-block:: java

    public static final DefaultParameterDescriptor<URI> FILE

El descriptor per al paràmetre "Latitude and longitude difference file".
El valor per defecte és "" (cadena buida).

Constructor
-----------

NTv2Transform.Provider
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public NTv2Transform.Provider()

Instancia aquest proveïdor.

Mètodes
-------

getOperationType
~~~~~~~~~~~~~~~~

.. code-block:: java

    public Class<Transformation> getOperationType()

Retorna el tipus d'operació.

Sobreescriu:

``getOperationType`` a la classe ``MathTransformProvider``

Retorna:

La interfície de la GeoAPI implementada per aquesta operació.

createMathTransform
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected MathTransform createMathTransform(ParameterValueGroup values)
                                         throws ParameterNotFoundException,
                                                FactoryException

Crea una transformació matemàtica a partir del grup de valors de paràmetres especificat.

Especificat a:

``createMathTransform`` a la classe ``MathTransformProvider``

Paràmetres:

``values`` - El grup de valors de paràmetres.

Retorna:

La transformació matemàtica.

Llença:

``ParameterNotFoundException`` - si no s'ha especificat algun dels paràmetres obligatoris.

``FactoryException`` - si hi ha hagut problemes instanciant la transformació.

