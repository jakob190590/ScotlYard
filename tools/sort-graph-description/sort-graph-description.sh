#!/bin/bash
# sort-graph-description.sh -- Sort the
# graph description passed by the first
# parameter
# Jakob Schöttl

mkdir -p tmp

cat "$1" | grep -E '^V' > tmp/vertices
cat "$1" | grep -E '^E' > tmp/edges

cd tmp
sort -n -k 3 vertices > vertices.s
sort -n -k 3,4 edges > edges.s
cat vertices.s edges.s > graph-description.s
cd ..

mv tmp/graph-description.s "$1"

# Aufraeumen, aber nicht einfach das Verzeichnis 
# loeschen falls es schon existiert
cd tmp
rm vertices
rm vertices.s
rm edges
rm edges.s
cd ..
rmdir tmp 2> /dev/null
