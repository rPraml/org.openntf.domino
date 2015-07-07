#!/bin/bash
find . -path "*/resources/META-INF/services" -exec update_plugin_xml_helper.sh {} \;