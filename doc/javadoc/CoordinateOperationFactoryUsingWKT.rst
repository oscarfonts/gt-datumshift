CoordinateOperationFactoryUsingWKT
==================================

.. code-block:: java

   package org.geotools.referencing.factory.epsg 

Herència::

    Object
      AbstractFactory
          ReferencingFactory
              AbstractAuthorityFactory
                  BufferedAuthorityFactory
                      DeferredAuthorityFactory
                          CoordinateOperationFactoryUsingWKT

Signatura:

.. code-block:: java

    public class CoordinateOperationFactoryUsingWKT
            extends DeferredAuthorityFactory
            implements CoordinateOperationAuthorityFactory

Descripció:

Factoria que gestiona operacions de coordenades definides per l'usuari.

Aquesta factoria es pot utilitzar per proveïr operacions de coordenades quan
no hi ha accés a una base de dades EPSG completa.

O, assignant-hi una prioritat més alta, es pot utilitzar per
redefinir o ampliar les operacions de coordenades existents a EPSG.

Les operacions de coordenades es defineixen com una MathTransform definida
en Well Known Test (vegeu detalls i exemples a ``PropertyCoordinateOperationAuthorityFactory``).

El fitxer de propietats portarà el nom :file:`epsg_operations.properties`, i les
seves possibles localitzacions es descriuen a `FILENAME`_.

Si no es troba cap fitxer de propietats, aquesta factoria no s'activarà.

Si no es troba una operació al fitxer de propietats, aquesta factoria delegarà
la creació a una factoria alternativa. La factoria alternativa utilitzada serà
la factoria de tipus ``CoordinateOperationAuthorityFactory`` per a l'autoritat *EPSG*
que es trobi a continuació d'aquesta en l'ordre de prioritats.

Autor:
    Oscar Fonts

Atributs
--------

authority
~~~~~~~~~

.. code-block:: java

    protected Citation authority

L'autoritat. Només es crearà quan es necessiti per primera vegada.

Vegeu també:

`getAuthority`_.

FILENAME
~~~~~~~~

.. code-block:: java

    public static final String FILENAME

El fitxer de definicions per defecte.

Aquesta implementació cercarà aquest fitxer en els llocs següents:

* Al directori especificat per la propietat de sistema
   `org.geotools.referencing.crs-directory`.
* Als directoris :file:`org/geotools/referencing/factory/espg` dintre del *classpath*.

Vegeu també:

`getDefinitionsURL`_,

PRIORITY
~~~~~~~~

.. code-block:: java

    public static final int PRIORITY

Prioritat per aquesta factoria


factories
~~~~~~~~~

.. code-block:: java

    protected final ReferencingFactoryContainer factories

Les factories que es passaràn al *backing store*.


directory
~~~~~~~~~

.. code-block:: java

    protected final String directory

Directori on es cercaràn les definicions extra.


fallbackAuthorityFactory
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected CoordinateOperationAuthorityFactory fallbackAuthorityFactory

Una factoria alternativa a utilitzar quan la primària no trobi una operació.

fallbackAuthorityFactorySearched
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected boolean fallbackAuthorityFactorySearched

Un indicador per cercar aquesta factoria només un cop.

Constructors
------------

CoordinateOperationFactoryUsingWKT
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public CoordinateOperationFactoryUsingWKT()

Constructor utilitzant les factories subjacents per defecte.

CoordinateOperationFactoryUsingWKT
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public CoordinateOperationFactoryUsingWKT(Hints userHints)

Constructor utilitzant les factories subjacent creades a partir dels hints especificats.

CoordinateOperationFactoryUsingWKT
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected CoordinateOperationFactoryUsingWKT(Hints userHints,
                                                 int priority)

Constructor utilitzant els hints especifiats i la prioritat especificada.

Mètodes
-------

getAuthority
~~~~~~~~~~~~

.. code-block:: java

    public Citation getAuthority()

Retorna la organització o agent responsable de la definició i manteniment de la base de dades subjacent.

Especificat a:

``getAuthority`` a la interfície ``AuthorityFactory``

Sobreescriu:

``getAuthority`` a la classe ``BufferedAuthorityFactory``


createBackingStore
~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected AbstractAuthorityFactory createBackingStore()
                                                   throws FactoryException

Crea la *backing store*.

Especificat a:

``createBackingStore`` a la classe ``DeferredAuthorityFactory``

Retorna:

La *backing store* que s'utilitzarà als mètodes ``createXXX(...)``.

Llença:

``FactoryNotFoundException`` - si el fitxer de ``properties`` no s'ha trobat.

``FactoryException`` - Si el constructor no ha aconseguit trobar o llegir
el fitxer. Aquesta excepcio generalment està causada per una 
``IOException``.

getDefinitionsURL
~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected URL getDefinitionsURL()

Retorna la URL apuntant al fitxer de propietats que conté les definicions de les operacions.
Aquesta implementació realitza la cerca del fitxer de la següent manera:

* Si s'ha informat la propietat de sistema `org.geotools.referencing.crs-directory`,
  es cercarà el :file:`epsg_operations.properties` en aquest directori.
* Si no s'ha indicat la propietat mencionada, o si no es troba el fitxer de
  propietats en aquella localització, llavors s'utilitzarà el primer fitxer
  anomenat :file:`epsg_operations.properties` dins de
  :file:`org/geotools/referencing/factory/epsg` al *classpath*.
* Si tampoc es troba aquest fitxer al *classpath*, es deshabilitarà aquesta factoria.

Retorna:

La URL, o ``null`` si no s'ha trobat.

createFromCoordinateReferenceSystemCodes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
                String sourceCRS, String targetCRS)
                    throws NoSuchAuthorityCodeException, FactoryException

Crea operacions a partir dels sistemes de referència de coordenades donats.
Aquest mètode cerca al fitxer de propietats `FILENAME`_.

Si no es troba la operació, s'utilitzarà una factoria alternativa mitjançant
`getFallbackAuthorityFactory`_.

Especificat a

``createFromCoordinateReferenceSystemCodes``
a la interfície
``CoordinateOperationAuthorityFactory``

Sobreescriu:

``createFromCoordinateReferenceSystemCodes`` a la classe ``BufferedAuthorityFactory``

Paràmetres:

``sourceCRS`` - Identificador del sistema de referència de coordenades d'origen.

``targetCRS`` - Identificador del sistema de referència de coordenades destí.

Retorna:

Les operacions trobades de ``sourceCRS`` a ``targetCRS``.

Llença:

``NoSuchAuthorityCodeException`` - si algun dels identificadors especificats no s'ha pogut trobar.

``FactoryException`` - si la creació de l'objecte ha fallat per qualsevol altra raó.

createCoordinateOperation
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public CoordinateOperation createCoordinateOperation(String code)
                throws NoSuchAuthorityCodeException, FactoryException

Crea una operació a partir del seu identificador.

Aquest mètode cerca al fitxer de propietats `FILENAME`_.

Si no es troba la operació, s'utilitzarà una factoria alternativa mitjançant
`getFallbackAuthorityFactory`_.

Especificat a:

``createCoordinateOperation`` a la interfície ``CoordinateOperationAuthorityFactory``

Sobreescriu:

``createCoordinateOperation`` a la classe ``BufferedAuthorityFactory``

Paràmetres:

``code`` - Identificador de la operació.

Retorna:

La operació des de ``sourceCRS`` a ``targetCRS`` (un únic element).

Llença:

``NoSuchAuthorityCodeException`` - si algun dels identificadors especificats no s'ha pogut trobar.

``FactoryException`` - si la creació de l'objecte ha fallat per qualsevol altra raó.


getFallbackAuthorityFactory
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    protected CoordinateOperationAuthorityFactory getFallbackAuthorityFactory()
                throws NoSuchAuthorityCodeException, FactoryException

Obté la següent ``CoordinateOperationAuthorityFactory`` amb més prioritat després d'aquesta.

Retorna:

La ``CoordinateOperationAuthorityFactory`` alternativa.

Llença:

``NoSuchAuthorityCodeException`` - si algun dels identificadors especificats no s'ha pogut trobar.

``FactoryException`` - si la creació de l'objecte ha fallat per qualsevol altra raó.

