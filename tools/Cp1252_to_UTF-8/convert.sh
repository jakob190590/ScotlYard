#!/bin/bash
# convert.sh -- Converts Cp1252 to UTF-8
# Jakob Schöttl

# !! NOT TESTED YET

find . -name *.java | while read f
do
  sed -f sed_program < $f > tmputf8convert && cp tmputf8convert $f
done
rm tmputf8convert
