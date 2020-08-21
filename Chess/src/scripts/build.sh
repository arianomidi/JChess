#!/bin/bash

build(){
	curdir=`pwd`

	cd /Users/arianomidi/Dropbox/Personal/Code/Java/ChessApplication/Chess
	
	#if [[ -d build ]]; then
	#	rm -r build
	#fi
	
	cp -r src/main/java/ build/
	cp -r resources build/
	
	cd build
	javac ChessApplication.java

	cd $curdir
}

chess(){
	curdir=`pwd`

	cd /Users/arianomidi/Dropbox/Personal/Code/Java/ChessApplication/Chess/build

	java ChessApplication
	
	cd $curdir
}

#build
