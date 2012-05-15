NTv2GridShiftFactory
====================

.. code-block:: java

   package org.geotools.referencing.factory.gridshift

Herència::

    Object
      AbstractFactory
          ReferencingFactory
              NTv2GridShiftFactory

Signatura:

.. code-block:: Java

   public class NTv2GridShiftFactory extends ReferencingFactory implements BufferedFactory

Descripció:

Carrega malles NTv2. Com que la càrrega és un procés costós en temps i memòria, utilitza un mecanisme de `caché` flexible, per mantenir les malles carregades en memòria mentre sigui possible. També comprova la integritar del fitxer NTv2 sense haver de procedir a una càrrega completa de la malla, al mètode `isNTv2Grid`_.

Autor:

Oscar Fonts

Atributs
--------

LOGGER
~~~~~~

.. code-block:: java

   protected static final Logger LOGGER

Logger.

Constructors
------------

NTv2GridShiftFactory
~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public NTv2GridShiftFactory()

Instancia la factoria amb la prioritat per defecte.

NTv2GridShiftFactory
~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public NTv2GridShiftFactory(int priority)

Instancia la factoria amb la prioritat donada.

Paràmetres:

  ``priority`` - La prioritat de la factoria, amb un número entre ``MINIMUM_PRIORITY`` i ``MAXIMUM_PRIORITY``.

Mètodes
-------

isNTv2Grid
~~~~~~~~~~

.. code-block:: java

   public boolean isNTv2Grid(URL location)

Localitza el fitxer de malla, i comprova la seva integritat sense procedir a una càrrega de la malla.

Paràmetres:

``name`` - The NTv2 grid file name

Retorn:

``true`` si el fitxer existeix i és vàlid, ``false`` altrament.

createNTv2Grid
~~~~~~~~~~~~~~

.. code-block:: java

   public GridShiftFile createNTv2Grid(URL gridLocation) throws FactoryException

Crea una malla NTv2 en memòria. Si la malla ja ha estat carregada anteriorment, es retorna la instància mantinguda en `caché`.

Paràmetres:

``gridLocation`` - El nom del fitxer.

Retorn:

Una estructura de dades amb la malla.

Llença:

``FactoryException`` - si la malla no s'ha pogut crear.

isNTv2GridFileValid
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   protected boolean isNTv2GridFileValid(URL url)

Comprova si el recurs és un fitxer NTv2 vàlid sense carregar-lo completament en memòria. Si el fitxer no és vàlid, la causa de l'error es registra com un `warning <http://java.sun.com/j2se/1.5/docs/api/java/util/logging/Level.html?is-external=true#WARNING>`_.

Paràmetres:

``location`` - El `path` absolut al fitxer NTv2.

Retorn:

`true` si el fitxer té un format NTv2 vàlid, `false` altrament.
