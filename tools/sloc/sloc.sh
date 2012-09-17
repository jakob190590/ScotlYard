#!/bin/bash
# sloc.sh -- Determines source lines of code
# Jakob Schöttl

# Komplette Ausgabe aller Java-Dateien
# über sed an wc
find . -name *.java | while read f
do
  cat "$f"
done | sed '/^\s*$/d' | wc -l
