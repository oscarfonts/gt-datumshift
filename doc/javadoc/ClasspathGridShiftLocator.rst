ClasspathGridShiftLocator
=========================

.. code-block:: java

   package org.geotools.referencing.factory.gridshift 


Herència::

    Object
      AbstractFactory
          ClasspathGridShiftLocator

Signatura:

.. code-block:: java

    public class ClasspathGridShiftLocator extends AbstractFactory implements GridShiftLocator

Localitzador de fitxers de malla per defecte, cerca al *classpath*.

Autor:
    Andrea Aime - GeoSolutions

Constructor
-----------

ClasspathGridShiftLocator
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public ClasspathGridShiftLocator()

Mètodes
-------

getVendor
~~~~~~~~~

.. code-block:: java

    public Citation getVendor()

locateGrid
~~~~~~~~~~

.. code-block:: java

    public URL locateGrid(String grid)


Localitza el recurs especificat.

Especificat a:

``locateGrid`` a la interfície ``GridShiftLocator``

Paràmetres:

``grid`` - el nom de la malla

Retorna:

La localització completa del recurs, o ``null`` si el recurs no s'ha pogut localitzar.

