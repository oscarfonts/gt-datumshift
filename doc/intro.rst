Introducció
===========

El *Real Decreto 1071/2007*, estableix el sistema ETRS89 com el sistema geodèsic oficial per a Espanya. A partir de l’1 de gener de 2012, tot nou projecte inscrit al Registre Central de Cartografia ha d’estar referit a aquest nou sistema de referència, i la producció existent s’haurà d’anar adaptant durant un període de transició durant el qual ETRS89 conviurà amb l’antic sistema ED50. El període de transició finalitza l’1 de gener del 2015.

L’Institut Cartogràfic de Catalunya ha establert una transformació bidimensional de semblança aplicable a la cartografia d’escales fins a 1:1 000, catalogada amb el codi EPSG:5166, que permet la transformació entre els sistemes de referència ED50 i ETRS89. Molts dels productes de software existents no suporten aquest tipus de transformació bidimensional directa, però sí que poden incorporar malles de transformació en format NTv2, per la qual cosa, l’ICC també ha publicat la transformació en aquest format.

Per tal de poder utilitzar aquesta transformació a les aplicacions existents, cal que al menys tinguin la capacitat de realitzar transformacions de malla basades en el format NTv2. La llibreria de codi lliure GeoTools, en la que es basen nombroses aplicacions Java com el servidor de mapes GeoServer o el SIG d’escriptori uDig, encara no disposava d’aquesta capacitat.

S'ha incorporat a GeoTools i a GeoServer la capacitat de transformació basada en malles NTv2, i a més s'ha incorporat la transformació oficial de semblança, que resulta més eficient per a transormacions directes en la projecció UTM.

Les properes versions de GeoTools 8.0 i GeoServer 2.2, actualment en fase de proves, incorporaràn aquestes noves capacitats.

Tasques realitzades
-------------------

#. Incorporació de la llibreria `jGridShift` com un nou transformador dins del mòdul de referenciació espacial de GeoTools.
#. Establiment dels mecanismes per declarar l’existència de malles de transformació a diferents ubicacions, depenent del context d’execució.
#. Modificació de l’algorisme que escull la transformació a emprar en cada moment. Contempla dos possibles escenaris:

   * Descoberta automàtica de la transformació més adient, a partir de la base de dades EPSG.
   * Forçat de l’ús d’una transformació concreta, configurable per l’usuari.

#. Integració d’aquesta nova funcionalitat base al projecte GeoServer.
#. Desenvolupament de tests unitaris per ambdós projectes, mitjançant `JUnit`.
#. Documentació del codi per a desenvolupadors, mitjançant `Javadoc`.
#. Ampliació del manual d'usuari (oficial, en anglès) de GeoServer.
#. Seguiment de la incorporació del nou codi i documentació a les següents versions oficials d’ambdós projectes, incloent-hi suport tècnic i seguiment de les proves realitzades per altres usuaris en les fases *beta* dels productes.
#. Tests de precisió i rendiment de l’aplicació, en tres escenaris:

   * Transformació de 7 paràmetres sel·leccionada per GeoServer en l'escenari previ a aquest desenvolupament.
   * Transformació de semblança bidimensional EPSG:5166.
   * Transformació mitjançant la malla NTv2 `100800401.gsb`.

#. Tests d'integració funcional a GeoServer.
#. Redacció d’aquest informe.

El codi i documentació aportats als projectes reste sotmesos a les llicències i condicions d’ús establertes als projectes als quals s’incorporen: LGPL per al codi de GeoTools, i GPL per al codi de GeoServer.

El codi i documentació oficials dels projectes es poden consultar a partir dels respectius llocs web:

* GeoTools: http://www.geotools.org
* GeoServer: http://www.geoserver.org

Condicions d'ús i reutilització d'aquest document
-------------------------------------------------

Aquest document està subjecte a una llicència `Reconeixement-CompartirIgual 3.0 No adaptada de Creative Commons <http://creativecommons.org/licenses/by-sa/3.0/>`_.

.. figure:: images/CC-by-sa.png
   :align: center

Sou lliures de:

   * Copiar, distribuir i comunicar públicament l'obra.
   * Fer-ne obres derivades.
   * Fer un ús comercial de l'obra.

Amb les condicions següents:

   * **Reconeixement**: Heu de reconèixer els crèdits de l'obra de la manera especificada per l'autor o el llicenciador (però no d'una manera que suggereixi que us donen suport o rebeu suport per l'ús que feu l'obra).
   * **Compartir Igual**: Si altereu o transformeu aquesta obra, o en genereu obres derivades, només podeu distribuir l'obra generada amb una llicència idèntica a aquesta.

.. note::

   Quan reutilitzeu o distribuïu l'obra, heu de deixar ben clar els termes de la llicència de l'obra.
