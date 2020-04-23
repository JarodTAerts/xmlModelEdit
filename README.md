# XML Model Editor
This is a command line tool to adjust the merged xml models of android apks following theAndroid App Bundle (AAB) Paradigm. This tool removes or replaces the redundant component definitions in the base.xml of an AAB base apk with the concrete definitions which are in the split_feature.xml files of the AAB. 

You can either download the source to run the tool or download the jar file from the releases tab.

## Running the tool
In order to run this program you must provide at least 3 parameters in the following order. These are not named parameters:
        1. File path to base.xml file to be edited
        2. File path to folder containing all the split_apk.xml files for the base apk
        3. Either 'd' or 'r' which tells the tool whether to delete the components in the base.xml or to replace them.

Example call: java xmlModelEdit ./base.xml ./splitXmlFiles d