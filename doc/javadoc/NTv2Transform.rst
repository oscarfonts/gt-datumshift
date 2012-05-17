NTv2Transform
=============

.. code-block:: java

   package org.geotools.referencing.operation.transform 

Herència::

    Object
      Formattable
          AbstractMathTransform
              NTv2Transform

Signatura:

.. code-block:: java

    public class NTv2Transform extends AbstractMathTransform implements MathTransform2D, Serializable

Descripció:

Implementació del mètode de transformació de coordenades "NTv2" (EPSG:9615).

Aquesta transformació depèn d'un recurs extern (el fitxer de malla NTv2). Si el
fitxer no està disponible, es llençarà una excepció recuperable ``NoSuchIdentifierException``
en el moment de la instanciació.

Autor:

Oscar Fonts

Vegeu també:

*Exception Handling* a `IdentifiedObjectSet <http://docs.geotools.org/latest/javadocs/org/geotools/referencing/factory/IdentifiedObjectSet.html>`_,

Atributs
--------

LOGGER
~~~~~~

.. code-block:: java

    protected static final Logger LOGGER

Logger

Constructor
-----------

NTv2Transform
~~~~~~~~~~~~~

.. code-block:: java

    public NTv2Transform(URI file) throws NoSuchIdentifierException

Construeix una ``NTv2Transform`` a partir del fitxer de malla indicat.
Aquest constructor verifica que el recurs sigui localitzable i la seva integritat,
però no carrega la malla completa en memòria per estalviar recursos.

Paràmetres:

``file`` - NTv2 grid file name

Llença:

``NoSuchIdentifierException`` - if the grid is not available.

Mètodes
-------

hashCode
~~~~~~~~

.. code-block:: java

    public int hashCode()

Retorna un *hash* per a aquesta transformació, diferent entre instàncies que no
siguin equivalents.

Sobreescriu:

``hashCode`` a la classe ``AbstractMathTransform``

equals
~~~~~~

.. code-block:: java

    public boolean equals(Object object)

Compara l'objecte passat com a paràmentre amb aquest, i en determina l'equivalència.
``object`` es considerarà equivalent si:

* És aquesta mateixa instància (`object`` és ``this``).

* Si és una instància de ``NTv2Transform`` i els seus paràmetres tenen el mateix
  valor (en aquest cas, el mateix fitxer de malla).

Sobreescriu:

``equals`` a la classe ``AbstractMathTransform``

Paràmetres:

``object`` - The object to compare with this transform.

Retorna:

``true`` si l'objecte donat és ``this``, o una ``NTv2Transform`` amb els
mateixos valors de paràmetres. Implica que, donada una mateixa coordenada
d'entrada en ambdós objectes, les corresponents coordenades transformades també
serien idèntiques.

inverse
~~~~~~~

.. code-block:: java

    public MathTransform2D inverse()

Retorna la inversa d'aquesta trasformació.

Especificat a:

``inverse`` a la interfície ``MathTransform``

``inverse`` a la interfície ``MathTransform2D``

Sobreescriu:

``inverse`` a la classe ``AbstractMathTransform``

Retorna:

La inversa d'aquesta transformació.

transform
~~~~~~~~~

.. code-block:: java

    public void transform(double[] srcPts,
                          int srcOff,
                          double[] dstPts,
                          int dstOff,
                          int numPts)
                   throws TransformException

Transforma una llista de coordenades puntuals. Aquest mètode es proporciona
per transformar eficientment una col·lecció de punts.

El vector d'entrada proporcionat conté una serialització de les coordenades.
Per exemple, si les dimensions de les coordenades d'entrada són 2, els valors
apareixeran en aquest ordre:

(x\ :sub:`0`\ ,y\ :sub:`0`\ , x\ :sub:`1`\ ,y\ :sub:`1`\ ...).

Especificat a:

``transform`` a la interfície ``MathTransform``

Paràmetres:

``srcPts`` - el vector que conté les coordenades d'entrada.

``srcOff`` - la posició del primer punt a transformar dins el vector.

``dstPts`` - el vector al que s'escriuràn les coordenades transformades. Pot ser el mateix que ``srcPts``.

``dstOff`` - la primera posició del vector en la que es començaran a escriure els punts transformats.

``numPts`` - el número de punts a transformar.

Llença:

``TransformException`` - si s'esdevé un error en la càrrega del fitxer de malla
(probabilitat baixa, doncs s'ha comprovat la seva integritat en el constructor).

inverseTransform
~~~~~~~~~~~~~~~~

.. code-block:: java

    public void inverseTransform(double[] srcPts,
                                 int srcOff,
                                 double[] dstPts,
                                 int dstOff,
                                 int numPts)
                          throws TransformException

Transformació inversa. Vegeu `transform`_.

Paràmetres:

``srcPts`` - el vector que conté les coordenades d'entrada.

``srcOff`` - la posició del primer punt a transformar dins el vector.

``dstPts`` - el vector al que s'escriuràn les coordenades transformades. Pot ser el mateix que ``srcPts``.

``dstOff`` - la primera posició del vector en la que es començaran a escriure els punts transformats.

``numPts`` - el número de punts a transformar.

Llença:

``TransformException`` - si s'esdevé un error en la càrrega del fitxer de malla
(probabilitat escassa, doncs s'ha comprovat la seva integritat en el constructor).

getSourceDimensions
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public int getSourceDimensions()

La dimensió dels punts d'entrada (2).

Especificat a:

``getSourceDimensions`` a la interfície ``MathTransform``

``getSourceDimensions`` a la classe ``AbstractMathTransform``


getTargetDimensions
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public int getTargetDimensions()

La dimensió dels punts de sortida (2).

Especificat a:

``getTargetDimensions`` a la interfície ``MathTransform``

``getTargetDimensions`` a la classe ``AbstractMathTransform``

getParameterValues
~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public ParameterValueGroup getParameterValues()

Retorna els valors dels paràmetres per a aquesta transformació matemàtica.

Sobreescriu:

``getParameterValues`` a la classe ``AbstractMathTransform``

Retorna:

Una còpia dels valors dels paràmetres per a aquesta transformació matemàtica.

Vegeu tamé:

``Operation.getParameterValues()``

