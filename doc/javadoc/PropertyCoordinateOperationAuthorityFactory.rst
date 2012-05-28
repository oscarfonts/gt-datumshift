PropertyCoordinateOperationAuthorityFactory
===========================================

.. code-block:: java

   package org.geotools.referencing.factory 

Herència::

    Object
      AbstractFactory
          ReferencingFactory
              AbstractAuthorityFactory
                  DirectAuthorityFactory
                      PropertyCoordinateOperationAuthorityFactory

Signatura:

.. code-block:: java

    public class PropertyCoordinateOperationAuthorityFactory
        extends DirectAuthorityFactory
        implements CoordinateOperationAuthorityFactory

Descripció:

Una ``CoordinateOperationAuthorityFactory`` recolzada en un fitxer de *properties*.
Permet definir transformacions de coordenades personalitzades, a partir de
dos CRSs i una transformació matemàtica expressada en WKT.

Les entrades al fitxer de propietats prenen aquest format::

   [source crs code],[target crs code]=[WKT math transform]   

Exemples::

   4230,4258=PARAM_MT["NTv2", \
       PARAMETER["Latitude and longitude difference file", "100800401.gsb"]]
   23031,25831=PARAM_MT["Similarity transformation", \
       PARAMETER["Ordinate 1 of evaluation point in target CRS", -129.549], \
       PARAMETER["Ordinate 2 of evaluation point in target CRS", -208.185], \
       PARAMETER["Scale difference", 1.0000015504], \
       PARAMETER["Rotation angle of source coordinate reference system axes", 1.56504]]
     
Per a definicions més compactes, els noms dels paràmetres poden ser substituïts
pel seu identificador EPSG corresponent. Els exemples següents expressen les
mateixes transformacions que els anteriors::

   4230,4258=PARAM_MT["9615", PARAMETER["8656", "100800401.gsb"]]
   23031,25831=PARAM_MT["9621", \
       PARAMETER["8621", -129.549], \
       PARAMETER["8622", -208.185], \
       PARAMETER["8611", 1.0000015504], \
       PARAMETER["8614", 1.56504]]
     

Referències:

Vegeu `Well-Known Text format <http://www.geoapi.org/3.0/javadoc/org/opengis/referencing/doc-files/WKT.html>`_
per a la sintaxi de les transformacions de coordenades. Visiteu el registre `EPSG Geodetic Parameter
Registry <http://www.epsg-registry.org/>`_ per als noms, codis i valors permesos dels paràmetres.

Tingueu en compte que les transformacions invertibles s'utilitzaran en ambdues direccions.

Aquesta factoria no manté cap resultat en memòria. Qualsevol crida a un mètode ``createFoo``
donarà lloc a un nou processament de la definició en WKT. Per mantenir una caché, utilitzeu
aquesta factoria des d'alguna altra que proporcioni aquesta funcionalitat. Per exemple,
``BufferedAuthorityFactory``.

Autor:
    Oscar Fonts

Constructor
-----------

PropertyCoordinateOperationAuthorityFactory
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public PropertyCoordinateOperationAuthorityFactory(
                ReferencingFactoryContainer factories,
                Citation authority,
                URL definitions) throws IOException

Crea una factoria per a l'autoritat donada a partir del fitxer donat.

Paràmetres:

``factories`` - Les factories subjacents utilitzades per a la creació d'objectes.

``authority`` - La organització o agent responsable de la definició i manteniment de la base de dades.

``definitions`` - URL al fitxer de definicions.

Llença:

``IOException`` - si les definicions no poden ser llegides.

Mètodes
-------

createCoordinateOperation
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public CoordinateOperation createCoordinateOperation(String code)
                throws NoSuchAuthorityCodeException, FactoryException

Crea una operació a partir del seu identificador.

Especificat a:

``createCoordinateOperation`` a la interfície ``CoordinateOperationAuthorityFactory``

Sobreescriu:

``createCoordinateOperation`` a la classe ``AbstractAuthorityFactory``

Paràmetres:

``code`` - El codi per a la operació.

Retorna:

La operació per al codi donat.

Llença:

``NoSuchAuthorityCodeException`` - si no es troba una definicio per al codi especificat.

``FactoryException`` - si la creació de l'objecte ha fallat per qualsevol altra raó.


createFromCoordinateReferenceSystemCodes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
                String sourceCRS, String targetCRS)
                        throws NoSuchAuthorityCodeException, FactoryException

Crea una ``CoordinateOperation`` a partir d'un parell de codis CRS.
Aquest mètode retorna un a sola operació a partir del fitxer de *properties*.
Si la operació és invertible, també s'utilitzarà per resoldre la operació inversa.
Si no es troba cap operació, retornarà un conjunt buit.

Especificat a:

``createFromCoordinateReferenceSystemCodes``
a la interfície
``CoordinateOperationAuthorityFactory``

Sobreescriu:

``createFromCoordinateReferenceSystemCodes`` a la classe ``AbstractAuthorityFactory``

Paràmetres:

``sourceCRS`` - Identificador del sistema de referència de coordenades d'origen.

``targetCRS`` - Identificador del sistema de referència de coordenades destí.

Retorna:

La operació des de ``sourceCRS`` a ``targetCRS`` (un únic element).

Llença:

``NoSuchAuthorityCodeException`` - si algun dels identificadors especificats no s'ha pogut trobar.

``FactoryException`` - si la creació de l'objecte ha fallat per qualsevol altra raó.

getAuthorityCodes
~~~~~~~~~~~~~~~~~

.. code-block:: java

    public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> type)

Retorna el conjunbt d'identificadors del tipus especificat. Només s'acceptarà
com a tipus ``CoordinateOperation.class``. Aquesta factoria no filtrarà codis
per a les seves subclasses.

Especificat a:

``getAuthorityCodes`` a la interfície ``AuthorityFactory``

Paràmetres:

``type`` - El tipus de ``CoordinateOperation`` (o ``null``, que tindrà el mateix efecte).

Retorna:

Tots els codis disponibles, o el conjunt buit.


getDescriptionText
~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public InternationalString getDescriptionText(String code)
                                           throws NoSuchAuthorityCodeException,
                                                  FactoryException

Obté un text descriptiu d'un objecte corresponent al codi indicat.

Especificat a:

``getDescriptionText`` a la interfície ``AuthorityFactory``

Paràmetres:

``code`` - Codi proveït per l'autoritat.

Retorna:

Una descripció de l'objecte, o ``null`` si l'objecte corresponent a un determinat
codi no té descripció.

Llença:

``NoSuchAuthorityCodeException`` - si el codi especificat no s'ha trobat.

``FactoryException`` - si la cerca ha fallat per qualsevol altra raó.

getAuthority
~~~~~~~~~~~~

.. code-block:: java

    public Citation getAuthority()

Retorna:

La organització o agent responsable de la definició i manteniment de la base de dades.

Especificat a:

``getAuthority`` a la interfície ``AuthorityFactory``

``getAuthority`` a la classe ``AbstractAuthorityFactory``

