#!/bin/sh
# Entfernt aus allen Java-Dateien im und unterhalb
# des angegebenen Verzeichnisses die trailing
# whitespaces.
# Dieses Tool hat überhaupt keinen Sinn, weil Git
# nachher keine der Dateien wiedererkennt!


find $1 -type f -name *.java > javafiles
cat javafiles | while read f ; do
#  sed -i -E "s/[[:space:]]*$//" $f
#  sed -E "s/[[:space:]]*$//" $f > tmp && cp tmp $f
done
