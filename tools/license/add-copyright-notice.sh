#!/bin/bash
# add-cpright -- adds a copyright notice to a file
# Author: Jakob Schöttl
# 1th param: copyright-notice file
# 2nd param: file
# Example:
# find . -iname *.java -type f -print0 | xargs -0 -n 1 license/add-copyright-notice.sh license/copyright-notice-java.txt

NOTICE=$1
FILE=$2

cat $NOTICE $FILE > $FILE.new && mv $FILE.new $FILE
