#!/bin/sh
# Copy the binaries of ScotlYard
# Params:
# 1. root directory of the ScotlYard repo
# 2. root destination directory

mkdir -p "$2/Board/bin"
mkdir -p "$2/GraphBuilder"
mkdir -p "$2/Library"
mkdir -p "$2/ScotlYard"

cp "$1/Board/graph-description" "$2/Board/"
cp "$1/Board/initial-stations" "$2/Board/"
cp "$1/Board/original-scotland-yard-board.png" "$2/Board/"
cp "$1/Board/log4j.properties" "$2/Board/"
cp "$1/Board/Run_Board.bat" "$2/Board/"
cp -r "$1/Board/bin/" "$2/Board/"

cp "$1/GraphBuilder/Run_BuilderTool.bat" "$2/GraphBuilder/"
cp -r "$1/GraphBuilder/bin/" "$2/GraphBuilder/"

cp -r "$1/Library/" "$2/"

cp -r "$1/ScotlYard/bin/" "$2/ScotlYard/"