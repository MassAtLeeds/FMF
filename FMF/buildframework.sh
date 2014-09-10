
echo "---------------------------------------------------------------"
echo "***************************************************************"
echo "*        _________________        _________________           *"
echo "*        /___   ______    /      /    _____    ___/           * "
echo "*            [  ]     [    /    /    ]     [  ]               *"
echo "*            [  ]__   [  [  /  /  ]  ]   __[  ]               *"
echo "*            [   __]  [  ]/  //  /[  ]  [__   ]               *"
echo "*            [  ]     [  ] /____/ [  ]     [  ]               * "
echo "*            [__]     [__]        [__]     [__]               *"
echo "*                                                             *"
echo "*            FLEXIBLE    MOrmLING    FRAMEWORK               *"
echo "***************************************************************"
echo "*                                                             *"
echo "*  (c)MASS@LEEDS  HTTPS://GITHUB.COM/MassAtLeeds/FMF/         *"
echo "* GNU GENERAL PUBLIC LICENSE 3+  HTTP://WWW.GNU.ORG/licenses/ *"
echo "*                                                             *"
echo "***************************************************************"
echo "*                                                             *"
echo "*             Developers' version in Build/                   *"
echo "*             Distribution version in Dist/                   *"
echo "*             API Docs in Docs/                               *"
echo "*                                                             *"
echo "***************************************************************"
echo "---------------------------------------------------------------"

# ----------------------------------------------------------------
#  Batch file to build the Flexible Modelling Framework
#  The bat assumes the download as a Git Repository from:
#  In order, the file:
#		A) Constructs the relevant classpath
# 	B) Compiles the core files
#		C) Compiles specified plugin files
#	 	D) Builds the docs of core files 
#           plus specified plugins in a Docs directory
#		E) Creates a Build directory and fills it with the relevant jar files
#		F) Zips up the Build directory for distribution (Dist/fmf.zip), 
# 		  but excludes developer templates plugins. 
#	 To add new plugins, see comments below.
#   for key issue reporting lines, but off more generally.
#  Info: Andy Evans http://www.geog.leeds.ac.uk/people/a.evans
# ----------------------------------------------------------------


echo "---------------------------------------------------------------"
echo "Start build   "
echo "---------------------------------------------------------------"
echo "---------------------------------------------------------------"
echo "Set core classpaths "
echo "---------------------------------------------------------------"


export CLASSPATH=$CLASSPATH:FMFStart/src
export CLASSPATH=$CLASSPATH:FlexibleModellingFramework/src
export CLASSPATH=$CLASSPATH:Dependancies/
export CLASSPATH=$CLASSPATH:SharedObjects/src
export CLASSPATH=$CLASSPATH:Dependancies/beansbinding-1.2.1.jar
export CLASSPATH=$CLASSPATH:Dependancies/colt.jar
export CLASSPATH=$CLASSPATH:Dependancies/derby.jar
export CLASSPATH=$CLASSPATH:Dependancies/derbyclient.jar
export CLASSPATH=$CLASSPATH:Dependancies/derbynet.jar
export CLASSPATH=$CLASSPATH:Dependancies/ec_util.jar
export CLASSPATH=$CLASSPATH:Dependancies/opencsv-2.3.jar
export CLASSPATH=$CLASSPATH:Dependancies/swing-layout-1.0.4.jar
export CLASSPATH=$CLASSPATH:../../FMFStart/src
export CLASSPATH=$CLASSPATH:../../FlexibleModellingFramework/src
export CLASSPATH=$CLASSPATH:../../Dependancies/
export CLASSPATH=$CLASSPATH:../../SharedObjects/src
export CLASSPATH=$CLASSPATH:../../Dependancies/beansbinding-1.2.1.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/colt.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/derby.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/derbyclient.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/derbynet.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/ec_util.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/opencsv-2.3.jar
export CLASSPATH=$CLASSPATH:../../Dependancies/swing-layout-1.0.4.jar


echo "---------------------------------------------------------------"
echo "Compile core files   "
echo "---------------------------------------------------------------"
javac FlexibleModellingFramework/src/uk/ac/leeds/mass/fmf/framework/*.java
javac FlexibleModellingFramework/src/uk/ac/leeds/mass/fmf/data_management/*.java
javac FlexibleModellingFramework/src/FlatFile/*.java
javac FlexibleModellingFramework/src/MSAccess/*.java
javac FMFStart/src/fmfstart/*.java
javac SharedObjects/src/uk/ac/leeds/mass/fmf/fit_statistics/*.java
javac SharedObjects/src/uk/ac/leeds/mass/fmf/generic_algorithms/*.java
javac SharedObjects/src/uk/ac/leeds/mass/fmf/shared_objects/*.java


# ----------------------------------------------------------------
#  Compile plugins. Add javac call below for new plugins. 
#  If the plugin uses any code not in the plugin or core classpath, 
#  the classpath may need adjusting.
# ----------------------------------------------------------------
echo "---------------------------------------------------------------"
echo "Compile plugins  "
echo "---------------------------------------------------------------"

export CLASSPATH=$CLASSPATH:Plugins/Microsimulation/src

javac Plugins/Microsimulation/src/uk/ac/leeds/mass/fmf/microsimulation/*.java

export CLASSPATH=$CLASSPATH:Plugins/Graph/src

javac Plugins/Graph/src/uk/ac/leeds/mass/fmf/graph/*.java

export CLASSPATH=$CLASSPATH:Plugins/ClusterHunter/src

javac Plugins/ClusterHunter/src/uk/ac/leeds/mass/coordinates/*.java
javac Plugins/ClusterHunter/src/uk/ac/leeds/mass/fmf/clusterhunterui/*.java
javac Plugins/ClusterHunter/src/uk/ac/leeds/mass/cluster/*.java


export CLASSPATH=$CLASSPATH:PluginTemplates/ToolTemplates/src

javac PluginTemplates/ToolTemplates/src/tooltemplates/tooltemplate/*.java
javac PluginTemplates/ToolTemplates/src/tooltemplates/toolcommunication/*.java
javac PluginTemplates/ToolTemplates/src/tooltemplates/toolmenutemplate/*.java





#  ---------------------------------------------------------------
#  For new plugins, add the package to the PACKAGES variable, as 
#  below.
#  ---------------------------------------------------------------
echo "---------------------------------------------------------------"
echo "Build docs   "
echo "---------------------------------------------------------------"
mkdir Docs
# Core packages
export PACKAGES=
export PACKAGES=$PACKAGES:FlatFile
export PACKAGES=$PACKAGES:MSAccess
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.framework
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.data_management
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.shared_objects
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.generic_algorithms
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.fit_statistics
export PACKAGES=$PACKAGES:fmfstart
# Plugin packages
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.microsimulation
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.graph
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.coordinates
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.fmf.clusterhunterui
export PACKAGES=$PACKAGES:uk.ac.leeds.mass.cluster
export PACKAGES=$PACKAGES:tooltemplates.tooltemplate
export PACKAGES=$PACKAGES:tooltemplates.toolcommunication
export PACKAGES=$PACKAGES:tooltemplates.toolmenutemplate
# 2>NUL redirects StErr. Remove to see javadoc errors or 2> docerrors.txt to record them.

javadoc -d Docs -private -author -quiet -version -link http://docs.oracle.com/javase/8/docs/api/ -sourcepath %CLASSPATH% -subpackages $PACKAGES 2>NUL 




echo "---------------------------------------------------------------"
echo "Build core distribution jars   "
echo "Run exec.jar to run framework  "
echo "---------------------------------------------------------------"
mkdir Build
mkdir Build/lib
cp Dependancies/*.jar Build/lib/

echo "Building SharedObject.jar"
# Make the manifest if one not already existing in this location.
if EXIST SharedObjects/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: beansbinding-1.2.1.jar AbsoluteLayout.jar ec_util.jar>SharedObjects/MANifEST.MF"
export MFEXISTS=FALSE
)
cd SharedObjects/src
jar cmf ../MANifEST.MF ../../Build/lib/SharedObjects.jar uk/ac/leeds/mass/fmf/fit_statistics/*.class uk/ac/leeds/mass/fmf/generic_algorithms/*.class uk/ac/leeds/mass/fmf/shared_objects/*.class  
cd ../..
# If the manifest already existed, don't delete it.
if %MFEXISTS% == FALSE (
rm SharedObjects/MANifEST.MF
)


echo " Building FlexibleModellingFramework.jar"
if EXIST FlexibleModellingFramework/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: lib/swing-layout-1.0.4.jar lib/SharedObjects.jar lib/beansbinding-1.2.1.jar lib/derby.jar lib/derbyclient.jar lib/derbynet.jar lib/colt.jar lib/opencsv-2.3.jar lib/ec_util.jar>FlexibleModellingFramework/MANifEST.MF"
echo "Main-Class: uk.ac.leeds.mass.fmf.framework.StartUp>>FlexibleModellingFramework/MANifEST.MF"
export MFEXISTS=FALSE
)
cd FlexibleModellingFramework/src
jar cmf ../MANifEST.MF ../../Build/FlexibleModellingFramework.jar FlatFile/*.class MSAccess/*.class Resources/*.* uk/ac/leeds/mass/fmf/data_management/*.class uk/ac/leeds/mass/fmf/framework/*.class
cd ../..  
if %MFEXISTS% == FALSE (
rm FlexibleModellingFramework/MANifEST.MF
)


echo "Building FMFStart.jar"
# Make the manifest if one not already existing in this location.
if EXIST FMFStart/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: lib/swing-layout-1.0.4.jar lib/SharedObjects.jar lib/beansbinding-1.2.1.jar lib/derby.jar lib/derbyclient.jar lib/derbynet.jar lib/colt.jar lib/opencsv-2.3.jar lib/ec_util.jar>FMFStart/MANifEST.MF"
echo "Main-Class: fmfstart.Main>>FMFStart/MANifEST.MF"
export MFEXISTS=FALSE
)
cd FMFStart/src
jar cmf ../MANifEST.MF ../../Build/exec.jar fmfstart/*.class   
cd ../..
if %MFEXISTS% == FALSE (
rm FMFStart/MANifEST.MF
)


# ----------------------------------------------------------------
#  For new plugins, add jar making, as below.
#  MANifEST.MF made, used, and deleted to avoid disturbing 
#  those using various IDEs to compile and build.
#  Use greater-than symbol to create and overwrite, and two to 
#  create and append. 
# ----------------------------------------------------------------
echo "---------------------------------------------------------------"
echo "Build plugin jars"
echo "---------------------------------------------------------------"

echo "Building Microsimulation.jar"
if EXIST Plugins/Microsimulation/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: lib/SharedObjects.jar lib/swing-layout-1.0.3.jar lib/ec_util.jar>Plugins/Microsimulation/MANifEST.MF"
export MFEXISTS=FALSE
)
cd Plugins/Microsimulation/src
jar cmf ../MANifEST.MF ../../../Build/Microsimulation.jar uk/ac/leeds/mass/fmf/microsimulation/*.form uk/ac/leeds/mass/fmf/microsimulation/*.class
cd ../../..
if %MFEXISTS% == FALSE (
rm Plugins/Microsimulation/MANifEST.MF
)



echo "Building Graph.jar"
if EXIST Plugins/Graph/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: SharedObjects.jar>Plugins/Graph/MANifEST.MF"
export MFEXISTS=FALSE
)
cd Plugins/Graph/src
jar cmf ../MANifEST.MF ../../../Build/Graph.jar uk/ac/leeds/mass/fmf/graph/*.class uk/ac/leeds/mass/fmf/graph/*.properties graph/*.class
cd ../../..
if %MFEXISTS% == FALSE (
rm Plugins/Graph/MANifEST.MF
)



echo "Building ClusterHunter.jar"
if EXIST Plugins/ClusterHunter/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: SharedObjects.jar>Plugins/ClusterHunter/MANifEST.MF"
export MFEXISTS=FALSE
)
cd Plugins/ClusterHunter/src
jar cmf ../MANifEST.MF ../../../Build/ClusterHunter.jar uk/ac/leeds/mass/coordinates/*.class uk/ac/leeds/mass/fmf/clusterhunterui/*.class uk/ac/leeds/mass/cluster/*.class org/gavaghan/geodesy/*.class org/json/*.class org/json/*.class
cd ../../..
if %MFEXISTS% == FALSE (
rm Plugins/ClusterHunter/MANifEST.MF
)



echo "Building ToolTemplates.jar"
if EXIST PluginTemplates/ToolTemplates/MANifEST.MF (
export MFEXISTS=TRUE
) else (
echo "Class-Path: SharedObjects.jar>PluginTemplates/ToolTemplates/MANifEST.MF"
export MFEXISTS=FALSE
)
cd PluginTemplates/ToolTemplates/src
jar cmf ../MANifEST.MF ../../../Build/ToolTemplates.jar tooltemplates/tooltemplate/*.class tooltemplates/toolcommunication/*.class tooltemplates/toolmenutemplate/*.class tooltemplates/toolmenutemplate/*.gif
cd ../../..
if %MFEXISTS% == FALSE (
rm PluginTemplates/ToolTemplates/MANifEST.MF
)







echo "---------------------------------------------------------------"
echo "Copy essential non-jar files"
echo "---------------------------------------------------------------"
cp License.txt Build/License.txt
cp ReadmeToGetStarted.txt Build/ReadmeToGetStarted.txt
cp FlexibleModellingFramework/Init.txt Build/Init.txt

mkdir Build/handbooks-and-practicals

mkdir Build/handbooks-and-practicals/Microsimulation
cp -r "Plugins/Microsimulation/handbook-and-practicals/*" Build/handbooks-and-practicals/Microsimulation 

mkdir Build/handbooks-and-practicals/ClusterHunter
cp -r "Plugins/ClusterHunter/handbook-and-practicals/*" Build/handbooks-and-practicals/ClusterHunter 



echo "---------------------------------------------------------------"
echo "Zip for distribution   "
echo "See fmf.zip in Dist directory for full distribution in a zip  "
echo "---------------------------------------------------------------"
mkdir Dist

# Move any jars we don't want in distribution copy.
mv Build/ToolTemplates.jar Dist/ToolTemplates.jar

cd Build 
jar cf fmf.zip *
cd ..
mv Build/fmf.zip Dist/fmf.zip

# Move any jars back we don't want in distribution copy.
mv Dist/ToolTemplates.jar Build/ToolTemplates.jar



echo "---------------------------------------------------------------"
echo "Cleanup  "
echo "---------------------------------------------------------------"


echo "---------------------------------------------------------------"
echo "Done  "
echo "---------------------------------------------------------------"
echo "///////////////////////////////////////////////////////////////"
echo "---------------------------------------------------------------"

