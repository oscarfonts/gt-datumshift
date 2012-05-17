GridShiftLocator
================

.. code-block:: java

   package org.geotools.referencing.factory.gridshift 

Superinterfícies:

   Factory

Implementacions:

``ClasspathGridShiftLocator``


Signatura:

.. code-block:: java

    public interface GridShiftLocator extends Factory

Descripció:

Proporciona un punt de recolzament per a la localització de fitxers de malla
NTv1, NTv2 i NADCON

Autor:

Andrea Aime - GeoSolutions

Mètode
------

locateGrid
~~~~~~~~~~

.. code-block:: java

    URL locateGrid(String grid)

Localitza la malla especificada.

Paràmetres:

``grid`` - el nom de la malla

Retorna:

La localització completa del recurs, o ``null`` si el recurs no s'ha pogut localitzar.

