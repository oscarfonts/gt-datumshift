Introducció
===========

El Real Decreto 1071/2007, estableix el sistema ETRS89 com el sistema geodèsic oficial per a Espanya. A partir de l’1 de gener de 2012, tot nou projecte inscrit al Registre Central de Cartografia ha d’estar referit a aquest nou sistema de referència, i la producció existent s’haurà d’anar adaptant durant un període de transició durant el qual ETRS89 conviurà amb l’antic sistema ED50. El període de transició finalitza l’1 de gener del 2015.

L’Institut Cartogràfic de Catalunya ha establert una transformació bidimensional de semblança aplicable a la cartografia d’escales fins a 1:1 000, catalogada amb el codi EPSG:5166, que permet la transformació entre els sistemes de referència ED50 i ETRS89. Molts dels productes de software existents no suporten aquest tipus de transformació bidimensional directa, però sí que poden incorporar malles de transformació en format NTv2, per la qual cosa, l’ICC també ha publicat la transformació en aquest format.

Per tal de poder utilitzar aquesta transformació a les aplicacions existents, cal que al menys tinguin la capacitat de realitzar transformacions de malla basades en el format NTv2. La llibreria de codi lliure Geotools, en la que es basen nombroses aplicacions Java com el servidor de mapes Geoserver o el SIG d’escriptori uDig, encara no disposava d’aquesta capacitat.

S'ha incorporat a GeoTools i a GeoServer la capacitat de transformació basada en malles NTv2, i a més s'ha incorporat la transformació de semblança directa, que resulta més eficient per a transormacions directes en la projecció UTM.

Les properes versions de GeoTools 8.0 i GeoServer 2.2, actualment en fase de proves, incorporaràn aquestes noves capacitats.

Tasques realitzades
-------------------

#. Incorporació de la llibreria jGridShift com un nou transformador dins del mòdul de referenciació espacial de GeoTools.
#. Establiment dels mecanismes per declarar l’existència de reixetes de transformació a diferents ubicacions, depenent del context d’execució.
#. Modificació de l’algorisme que escull la transformació a emprar en cada moment. Contempla dos possibles escenaris:
   * Descoberta automàtica de la transformació més adient, a partir de la base de dades EPSG.
   * Forçat de l’ús d’una transformació concreta, configurable per l’usuari.
#. Integració d’aquesta nova funcionalitat base al projecte Geoserver, perquè es puguo.
#. Desenvolupament de tests unitaris per ambdós projectes, mitjançant junit.
#. Documentació del codi per a desenvolupadors, mitjançant javadoc.
#. Ampliació del manual d'usuari (oficial, en anglès) de GeoServer.
#. Seguiment de la incorporació del nou codi i documentació a les següents versions oficials d’ambdós projectes, incloent-hi suport tècnic i seguiment de les proves realitzades per altres usuaris en les fases *beta* dels productes.
#. Tests de precisió i rendiment de l’aplicació amb tres escenaris:
   * Transformació de 7 paràmentres en coordenades geocèntriques (situació anterior).
   * Transformació mitjançant malla NTv2 (coordenades geodèsiques).
   * Transformació de semblança bidimensional (coordenades UTM).
#. Redacció i publicació d’aquest manual.

El codi i documentació resten sotmesos a les llicències i condicions d’ús establerts als projectes als quals s’incorporen: LGPL per a GeoTools, i GPL per a GeoServer.

El codi i documentació oficials dels projectes es poden consultar a partir dels respectius llocs web:

* GeoTools: http://www.geotools.org
* GeoServer: http://www.geoserver.org

Seccions d'aquest document
--------------------------

* Diagrames de classes, javadoc, interacció (UML).
* Tests de precisió i rendiment. 
* Manual d'ús (traducció de l'oficial).
