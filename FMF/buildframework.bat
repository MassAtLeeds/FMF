ECHO OFF
ECHO ---------------------------------------------------------------
ECHO ***************************************************************
ECHO *        _________________        _________________           *
ECHO *        \___   ______    \      /    _____    ___/           * 
ECHO *            [  ]     [    \    /    ]     [  ]               *
ECHO *            [  ]__   [  [  \  /  ]  ]   __[  ]               *
ECHO *            [   __]  [  ]\  \/  /[  ]  [__   ]               *
ECHO *            [  ]     [  ] \____/ [  ]     [  ]               * 
ECHO *            [__]     [__]        [__]     [__]               *
ECHO *                                                             *
ECHO *            FLEXIBLE    MODELLING    FRAMEWORK               *
ECHO ***************************************************************
ECHO *                                                             *
ECHO *  (c)MASS@LEEDS  HTTPS://GITHUB.COM/MassAtLeeds/FMF/         *
ECHO * GNU GENERAL PUBLIC LICENSE 3+  HTTP://WWW.GNU.ORG/licenses/ *
ECHO *                                                             *
ECHO ***************************************************************
ECHO *                                                             *
ECHO *             Developers' version in Build/                   *
ECHO *             Distribution version in Dist/                   *
ECHO *             API Docs in Docs/                               *
ECHO *                                                             *
ECHO ***************************************************************
ECHO ---------------------------------------------------------------

REM ----------------------------------------------------------------
REM  Batch file to build the Flexible Modelling Framework
REM  The bat assumes the download as a Git Repository from:
REM  In order, the file:
REM		A) Constructs the relevant classpath
REM 	B) Compiles the core files
REM		C) Compiles specified plugin files
REM	 	D) Builds the docs of core files 
REM           plus specified plugins in a Docs directory
REM		E) Creates a Build directory and fills it with the relevant jar files
REM		F) Zips up the Build directory for distribution (Dist/fmf.zip), 
REM 		  but excludes developer templates plugins. 
REM	 To add new plugins, see comments below.
REM  ECHO ON for key issue reporting lines, but off more generally.
REM  Info: Andy Evans http://www.geog.leeds.ac.uk/people/a.evans
REM ----------------------------------------------------------------


ECHO ---------------------------------------------------------------
ECHO Start build   
ECHO ---------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Set core classpaths 
ECHO ---------------------------------------------------------------

SETLOCAL
SET CLASSPATH=%CLASSPATH%;FMFStart\src
SET CLASSPATH=%CLASSPATH%;FlexibleModellingFramework\src
SET CLASSPATH=%CLASSPATH%;Dependancies\
SET CLASSPATH=%CLASSPATH%;SharedObjects\src
SET CLASSPATH=%CLASSPATH%;Dependancies\beansbinding-1.2.1.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\colt.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\derby.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\derbyclient.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\derbynet.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\ec_util.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\opencsv-2.3.jar
SET CLASSPATH=%CLASSPATH%;Dependancies\swing-layout-1.0.4.jar
SET CLASSPATH=%CLASSPATH%;..\..\FMFStart\src
SET CLASSPATH=%CLASSPATH%;..\..\FlexibleModellingFramework\src
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\
SET CLASSPATH=%CLASSPATH%;..\..\SharedObjects\src
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\beansbinding-1.2.1.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\colt.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\derby.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\derbyclient.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\derbynet.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\ec_util.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\opencsv-2.3.jar
SET CLASSPATH=%CLASSPATH%;..\..\Dependancies\swing-layout-1.0.4.jar


ECHO ---------------------------------------------------------------
ECHO Compile core files   
ECHO ---------------------------------------------------------------
javac FlexibleModellingFramework\src\uk\ac\leeds\mass\fmf\framework\*.java
javac FlexibleModellingFramework\src\uk\ac\leeds\mass\fmf\data_management\*.java
javac FlexibleModellingFramework\src\FlatFile\*.java
javac FlexibleModellingFramework\src\MSAccess\*.java
javac FMFStart\src\fmfstart\*.java
javac SharedObjects\src\uk\ac\leeds\mass\fmf\fit_statistics\*.java
javac SharedObjects\src\uk\ac\leeds\mass\fmf\generic_algorithms\*.java
javac SharedObjects\src\uk\ac\leeds\mass\fmf\shared_objects\*.java


REM ----------------------------------------------------------------
REM  Compile plugins. Add javac call below for new plugins. 
REM  If the plugin uses any code not in the plugin or core classpath, 
REM  the classpath may need adjusting.
REM ----------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Compile plugins  
ECHO ---------------------------------------------------------------

SET CLASSPATH=%CLASSPATH%;Plugins\Microsimulation\src
ECHO ON
javac Plugins\Microsimulation\src\uk\ac\leeds\mass\fmf\microsimulation\*.java
ECHO OFF

SET CLASSPATH=%CLASSPATH%;Plugins\Graph\src
ECHO ON
javac Plugins\Graph\src\uk\ac\leeds\mass\fmf\graph\*.java
ECHO OFF

SET CLASSPATH=%CLASSPATH%;Plugins\ClusterHunter\src
ECHO ON
javac Plugins\ClusterHunter\src\uk\ac\leeds\mass\coordinates\*.java
javac Plugins\ClusterHunter\src\uk\ac\leeds\mass\fmf\clusterhunterui\*.java
javac Plugins\ClusterHunter\src\uk\ac\leeds\mass\cluster\*.java
ECHO OFF

SET CLASSPATH=%CLASSPATH%;PluginTemplates\ToolTemplates\src
ECHO ON
javac PluginTemplates\ToolTemplates\src\tooltemplates\tooltemplate\*.java
javac PluginTemplates\ToolTemplates\src\tooltemplates\toolcommunication\*.java
javac PluginTemplates\ToolTemplates\src\tooltemplates\toolmenutemplate\*.java
ECHO OFF



REM  ---------------------------------------------------------------
REM  For new plugins, add the package to the PACKAGES variable, as 
REM  below.
REM  ---------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Build docs   
ECHO ---------------------------------------------------------------
MKDIR Docs
REM Core packages
SET PACKAGES=
SET PACKAGES=%PACKAGES%:FlatFile
SET PACKAGES=%PACKAGES%:MSAccess
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.framework
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.data_management
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.shared_objects
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.generic_algorithms
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.fit_statistics
SET PACKAGES=%PACKAGES%:fmfstart
REM Plugin packages
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.microsimulation
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.graph
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.coordinates
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.clusterhunterui
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.cluster
SET PACKAGES=%PACKAGES%:tooltemplates.tooltemplate
SET PACKAGES=%PACKAGES%:tooltemplates.toolcommunication
SET PACKAGES=%PACKAGES%:tooltemplates.toolmenutemplate
REM 2>NUL redirects StErr. Remove to see javadoc errors or 2> docerrors.txt to record them.
ECHO ON
javadoc -d Docs -private -author -quiet -version -link http://docs.oracle.com/javase/8/docs/api/ -sourcepath %CLASSPATH% -subpackages %PACKAGES% 2>NUL 
ECHO OFF



ECHO ---------------------------------------------------------------
ECHO Build core distribution jars   
ECHO Run exec.jar to run framework  
ECHO ---------------------------------------------------------------
MKDIR Build
MKDIR Build\lib
COPY Dependancies\*.jar Build\lib\

ECHO Building SharedObject.jar
REM Make the manifest if one not already existing in this location.
IF EXIST SharedObjects\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: beansbinding-1.2.1.jar AbsoluteLayout.jar ec_util.jar>SharedObjects\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD SharedObjects\src
jar cmf ..\MANIFEST.MF ..\..\Build\lib\SharedObjects.jar uk\ac\leeds\mass\fmf\fit_statistics\*.class uk\ac\leeds\mass\fmf\generic_algorithms\*.class uk\ac\leeds\mass\fmf\shared_objects\*.class  
CD ..\..
REM If the manifest already existed, don't delete it.
IF %MFEXISTS% == FALSE (
DEL SharedObjects\MANIFEST.MF
)


ECHO  Building FlexibleModellingFramework.jar
IF EXIST FlexibleModellingFramework\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: lib/swing-layout-1.0.4.jar lib/SharedObjects.jar lib/beansbinding-1.2.1.jar lib/derby.jar lib/derbyclient.jar lib/derbynet.jar lib/colt.jar lib/opencsv-2.3.jar lib/ec_util.jar>FlexibleModellingFramework\MANIFEST.MF
ECHO Main-Class: uk.ac.leeds.mass.fmf.framework.StartUp>>FlexibleModellingFramework\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD FlexibleModellingFramework\src
jar cmf ..\MANIFEST.MF ..\..\Build\FlexibleModellingFramework.jar FlatFile\*.class MSAccess\*.class Resources\*.* uk\ac\leeds\mass\fmf\data_management\*.class uk\ac\leeds\mass\fmf\framework\*.class
CD ..\..  
IF %MFEXISTS% == FALSE (
DEL FlexibleModellingFramework\MANIFEST.MF
)


ECHO Building FMFStart.jar
REM Make the manifest if one not already existing in this location.
IF EXIST FMFStart\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: lib/swing-layout-1.0.4.jar lib/SharedObjects.jar lib/beansbinding-1.2.1.jar lib/derby.jar lib/derbyclient.jar lib/derbynet.jar lib/colt.jar lib/opencsv-2.3.jar lib/ec_util.jar>FMFStart\MANIFEST.MF
ECHO Main-Class: fmfstart.Main>>FMFStart\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD FMFStart\src
jar cmf ..\MANIFEST.MF ..\..\Build\exec.jar fmfstart\*.class   
CD ..\..
IF %MFEXISTS% == FALSE (
DEL FMFStart\MANIFEST.MF
)


REM ----------------------------------------------------------------
REM  For new plugins, add jar making, as below.
REM  MANIFEST.MF made, used, and deleted to avoid disturbing 
REM  those using various IDEs to compile and build.
REM  Use greater-than symbol to create and overwrite, and two to 
REM  create and append. 
REM ----------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Build plugin jars
ECHO ---------------------------------------------------------------

ECHO Building Microsimulation.jar
IF EXIST Plugins\Microsimulation\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: lib/SharedObjects.jar lib/swing-layout-1.0.3.jar lib/ec_util.jar>Plugins\Microsimulation\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD Plugins\Microsimulation\src
jar cmf ..\MANIFEST.MF ..\..\..\Build\Microsimulation.jar uk\ac\leeds\mass\fmf\microsimulation\*.form uk\ac\leeds\mass\fmf\microsimulation\*.class
CD ..\..\..
IF %MFEXISTS% == FALSE (
DEL Plugins\Microsimulation\MANIFEST.MF
)




ECHO Building Graph.jar
IF EXIST Plugins\Graph\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: SharedObjects.jar>Plugins\Graph\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD Plugins\Graph\src
jar cmf ..\MANIFEST.MF ..\..\..\Build\Graph.jar uk\ac\leeds\mass\fmf\graph\*.class uk\ac\leeds\mass\fmf\graph\*.properties graph\*.class
CD ..\..\..
IF %MFEXISTS% == FALSE (
DEL Plugins\Graph\MANIFEST.MF
)



ECHO Building ClusterHunter.jar
IF EXIST Plugins\ClusterHunter\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: SharedObjects.jar>Plugins\ClusterHunter\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD Plugins\ClusterHunter\src
jar cmf ..\MANIFEST.MF ..\..\..\Build\ClusterHunter.jar uk\ac\leeds\mass\coordinates\*.class uk\ac\leeds\mass\fmf\clusterhunterui\*.class uk\ac\leeds\mass\cluster\*.class org\gavaghan\geodesy\*.class org\json\*.class org\json\*.class
CD ..\..\..
IF %MFEXISTS% == FALSE (
DEL Plugins\ClusterHunter\MANIFEST.MF
)



ECHO Building ToolTemplates.jar
IF EXIST PluginTemplates\ToolTemplates\MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: SharedObjects.jar>PluginTemplates\ToolTemplates\MANIFEST.MF
SET MFEXISTS=FALSE
)
CD PluginTemplates\ToolTemplates\src
jar cmf ..\MANIFEST.MF ..\..\..\Build\ToolTemplates.jar tooltemplates\tooltemplate\*.class tooltemplates\toolcommunication\*.class tooltemplates\toolmenutemplate\*.class tooltemplates\toolmenutemplate\*.gif
CD ..\..\..
IF %MFEXISTS% == FALSE (
DEL PluginTemplates\ToolTemplates\MANIFEST.MF
)


ECHO ---------------------------------------------------------------
ECHO Copy essential non-jar files
ECHO ---------------------------------------------------------------
COPY License.txt Build\License.txt
COPY FlexibleModellingFramework\Init.txt Build\Init.txt

MKDIR Build\handbooks-and-practicals

MKDIR Build\handbooks-and-practicals\Microsimulation
XCOPY Plugins\Microsimulation\handbook-and-practicals\* Build\handbooks-and-practicals\Microsimulation\ /E

MKDIR Build\handbooks-and-practicals\ClusterHunter
XCOPY Plugins\ClusterHunter\handbook-and-practicals\* Build\handbooks-and-practicals\ClusterHunter\ /E


ECHO ---------------------------------------------------------------
ECHO Zip for distribution   
ECHO See fmf.zip in Dist directory for full distribution in a zip  
ECHO ---------------------------------------------------------------
MKDIR Dist

REM Move any jars we don't want in distribution copy.
MOVE Build\ToolTemplates.jar Dist\ToolTemplates.jar

CD Build 
jar cf fmf.zip *
CD ..
MOVE Build\fmf.zip Dist\fmf.zip

REM Move any jars back we don't want in distribution copy.
MOVE Dist\ToolTemplates.jar Build\ToolTemplates.jar



ECHO ---------------------------------------------------------------
ECHO Cleanup  
ECHO ---------------------------------------------------------------
ENDLOCAL

ECHO ---------------------------------------------------------------
ECHO Done  
ECHO ---------------------------------------------------------------
ECHO ///////////////////////////////////////////////////////////////
ECHO ---------------------------------------------------------------
ECHO ON