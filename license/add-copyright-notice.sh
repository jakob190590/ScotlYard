#!/bin/bash
# add-cpright -- adds a copyright notice to each java source file
# 1th param: copyright-notice file
# 2nd param: directory (optional)
# Author: Jakob Schöttl

NOTICE=$1
if test $# -ge 1
then
  DIR=$2
else
  DIR=.
fi

for f in $(find $DIR -iname *.java -type f)
do
  echo $f
  cat $NOTICE $f > $f.new && mv $f.new $f
done

