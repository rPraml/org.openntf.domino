#!/bin/bash
cd $1
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" > ../../plugin.xml
echo "<?eclipse version=\"3.4\"?>" >> ../../plugin.xml
echo "<plugin>" >> ../../plugin.xml
# quick & dirty. Valid lines starts with "org"
grep   "^org" * -H | gawk -F ":" '{ print "<extension point=\"com.ibm.commons.Extension\">"; \
				print "  <service class=\"" $2 "\"  type=\"" $1 "\"/>"; \
				print "</extension>";}' >> ../../plugin.xml
cat ../../plugin.add >> ../../plugin.xml
echo "</plugin>" >> ../../plugin.xml