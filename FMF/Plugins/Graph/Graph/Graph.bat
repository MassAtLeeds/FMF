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
ECHO *                       Graph Plugin                          *
ECHO *                                                             *
ECHO ***************************************************************
ECHO *                                                             *
ECHO *  (c)MASS@LEEDS  HTTPS://GITHUB.COM/MassAtLeeds/FMF/         *
ECHO * GNU GENERAL PUBLIC LICENSE 3+  HTTP://WWW.GNU.ORG/licenses/ *
ECHO *                                                             *
ECHO ***************************************************************
ECHO ---------------------------------------------------------------

REM ----------------------------------------------------------------
REM  Batch file to build the Graph Plugin for the 
REM  Flexible Modelling Framework
REM  Info: Andy Evans http://www.geog.leeds.ac.uk/people/a.evans
REM ----------------------------------------------------------------


ECHO ---------------------------------------------------------------
ECHO Start build. 
ECHO If this fails, you may need to edit this file to change 
ECHO PATHTOFMFROOT to point to root of FMF github download, i.e. 
ECHO the FMF directory with README.md in it. This can be relative 
ECHO to this file or absolute if easier.
ECHO ---------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Set core classpaths 
ECHO ---------------------------------------------------------------

SETLOCAL
REM Example of absolute path: SET PATHTOFMFROOT=c:\FMF\FMF
SET PATHTOFMFROOT=..\..\FMF\FMF

SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\SharedObjects\src
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\beansbinding-1.2.1.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\colt.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\derby.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\derbyclient.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\derbynet.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\ec_util.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\opencsv-2.3.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\Dependancies\swing-layout-1.0.4.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\FMFStart\src
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\FlexibleModellingFramework\src
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\SharedObjects\src
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\beansbinding-1.2.1.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\colt.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\derby.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\derbyclient.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\derbynet.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\ec_util.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\opencsv-2.3.jar
SET CLASSPATH=%CLASSPATH%;%PATHTOFMFROOT%\..\..\Dependancies\swing-layout-1.0.4.jar



REM ----------------------------------------------------------------
REM  Compile plugin.
REM  If the plugin uses any code not in the plugin or core classpath, 
REM  the classpath may need adjusting.
REM ----------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Compile plugin
ECHO ---------------------------------------------------------------

SET CLASSPATH=%CLASSPATH%;src
ECHO ON
javac src\uk\ac\leeds\mass\fmf\graph\*.java
ECHO OFF


REM  ---------------------------------------------------------------
REM  For new packages, add the package to the PACKAGES variable, as 
REM  below.
REM  ---------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Build docs   
ECHO ---------------------------------------------------------------
MKDIR docs
REM Plugin packages
SET PACKAGES=
SET PACKAGES=%PACKAGES%:uk.ac.leeds.mass.fmf.graph
REM 2>NUL redirects StErr. Remove to see javadoc errors or 2> docerrors.txt to record them.
ECHO ON
javadoc -d docs -private -author -quiet -version -link http://docs.oracle.com/javase/8/docs/api/ -sourcepath %CLASSPATH% -subpackages %PACKAGES% 2>NUL 
ECHO OFF


REM ----------------------------------------------------------------
REM  Building jar in FMF build directory.
REM  MANIFEST.MF made, used, and deleted to avoid disturbing 
REM  those using various IDEs to compile and build.
REM  Use greater-than symbol to create and overwrite, and two to 
REM  create and append. 
REM ----------------------------------------------------------------
ECHO ---------------------------------------------------------------
ECHO Build plugin jar in FMF build directory.
ECHO ---------------------------------------------------------------

ECHO Building Graph.jar

REM This fails rather than rebuilding if the directory exists, which is fine.
MKDIR %PATHTOFMFROOT%\Build

IF EXIST MANIFEST.MF (
SET MFEXISTS=TRUE
) ELSE (
ECHO Class-Path: lib/SharedObjects.jar>MANIFEST.MF
SET MFEXISTS=FALSE
)
CD src
jar cmf ..\MANIFEST.MF ..\%PATHTOFMFROOT%\Build\Graph.jar uk\ac\leeds\mass\fmf\graph\*.class uk\ac\leeds\mass\fmf\graph\*.properties graph\*.class
CD ..\
IF %MFEXISTS% == FALSE (
DEL MANIFEST.MF
)



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