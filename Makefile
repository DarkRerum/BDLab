all: compile run

LIBPATH = ./lib/ojdbc6.jar

DESTPATH = ./bin
CLASSPATH = ./bin:./bin/valve/steam:./bin/valve/util

compile:
	javac -cp .:$(CLASSPATH):$(LIBPATH) -d $(DESTPATH) ./src/valve/util/*.java	
	javac -cp .:$(CLASSPATH):$(LIBPATH) -d $(DESTPATH) ./src/valve/steam/*.java
	javac -cp .:$(CLASSPATH):$(LIBPATH) -d $(DESTPATH) ./src/*.java	
run:
	java -cp .:$(CLASSPATH):$(LIBPATH) Main

#SRCPATH = ./src:./src/valve/steam:./src/valve/util
#SRCFILES = ./src/*.java ./src/*/*/*.java
