  ---------------------------------------------------------------
  ***************************************************************
  *        _________________        _________________           *
  *        \___   ______    \      /    _____    ___/           * 
  *            [  ]     [    \    /    ]     [  ]               *
  *            [  ]__   [  [  \  /  ]  ]   __[  ]               *
  *            [   __]  [  ]\  \/  /[  ]  [__   ]               *
  *            [  ]     [  ] \____/ [  ]     [  ]               * 
  *            [__]     [__]        [__]     [__]               *
  *                                                             *
  *            FLEXIBLE    MODELLING    FRAMEWORK               *
  ***************************************************************
  *                                                             *
  *  (c)MASS@LEEDS  HTTPS://GITHUB.COM/MassAtLeeds/FMF/         *
  * GNU GENERAL PUBLIC LICENSE 3+  HTTP://WWW.GNU.ORG/licenses/ *
  *                                                             *
  ***************************************************************
  *                                                             *
  *                      Release version                        *
  *             				                                *
  *                                                             *
  ***************************************************************
  ---------------------------------------------------------------
  
 INSTALLING: 

 There's no need to install this software, just unzip it.

 To run: Run FlexibleModellingFramework.jar, for example, by 
 double-clicking it in a file explorer, my computer, etc. If this 
 doesn't work, try exec.jar.
 
 If it doesn't run: you may not have the Java Runtime (which this 
 uses) attached to ".jar" files. If this is the case, find the usual 
 way to choose how a file is run, and choose "javaw.jar" for the 
 FlexibleModellingFramework.jar file  as the default. 
 
 On Windows, this would involve right-clicking the 
 file in Windows Explorer, and choosing "Open with" and "Java(TM) 
 Platform SE binary", however, you may have to "Browse..." and 
 track down the javaw.exe file manually. For Windows users it will 
 usually be in "C:\Program Files\java\jre8\bin" or similar.
 
 If that doesn't work, you may need to install the Java Runtime. You 
 can download it here:
 https://java.com/en/download/index.jsp
 though most computers come with it.
 
  ---------------------------------------------------------------
 
 USE: 
 
 The software should be read to use. See below for details of 
 handbooks and practicals. 
 
 The software is a generic box for plugins that do various spatial 
 analysis tasks. It comes with all the usable plugins installed. 
 These are just .jar files in the directory you've unzipped. 
 If you don't want all  the plugins, you can remove them 
 by deleting the relevant .jar files from  this directoy. The core 
 application files that you should *not* delete are:
 
 FlexibleModellingFramework.jar
 and
 exec.jar (if you use it)
 
 All other ".jar" files you can delete if you don't want the 
 plugins. Save any data that needs saving, shut down the application, 
 delete the files, and restart.
 
 It should be obvious from the names which .jar file is which functionality, 
 but if it isn't you can experiment with moving the .jar files one at 
 a time somewhere else to see how the functionality changes.
 
  ---------------------------------------------------------------
 
 HANDBOOKS AND PRACTICALS:
 
 Each Plugin that has any associated handbooks or practicals will have 
 these, plus any associated example datasets, in the "Handbooks-and-practicals" 
 directory in the directory you've unzipped.
 
  ---------------------------------------------------------------
 
 DEVELOPERS:
 
 To develop your own plugins, or to contribute to the project, please see 
 details at HTTPS://GITHUB.COM/MassAtLeeds/FMF/ 
 If you build from the relevant scripts you get an additional plugin 
 containing exemplars of how to build various plugin elements that 
 you can use as templates.
   
 ---------------------------------------------------------------