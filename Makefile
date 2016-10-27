all: compile run

LIBPATH = ./lib/ojdbc6.jar

SRCPATH = ./src:./src/valve/steam:./src/valve/util

#SRCFILES = ./src/*.java ./src/*/*/*.java

compile:
	javac -cp .:$(SRCPATH):$(LIBPATH) -d ./src ./src/valve/util/*.java	
	javac -cp .:$(SRCPATH):$(LIBPATH) -d ./src ./src/valve/steam/*.java
	javac -cp .:$(SRCPATH):$(LIBPATH) ./src/*.java	
run:
	java -cp .:$(SRCPATH):$(LIBPATH) Main
