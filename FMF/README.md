This repository stores the FMF static spatial microsimulation software.

In the Binaries directory all of the compiled and bundled files are located.  Copying this folder onto a machine running ire 6 or later will enable the software to run.  To start the software simply click on the exec.jar file.  For full installation and user guidance please see the user guide document at:

In the src directory all of the source files required to build the project can be found in the correct directory structures reflecting the relevant classpaths. The software has dependencies on third party software:
   beansbinding-1.2.1
   colt
   derby
   derbyclient
   derbynet
   ec_util
   opencsv-2.3
   swing-layout-1.0.3
The associated jar files for these dependencies can be found in the Binaries/lib directory.

The test data directory contains a very simplified idealised example set of data.  This example is included so that users can see the file structures required to create a model.

