all: compile run

LIBPATH = ./lib/ojdbc6.jar

SRCPATH = ./src:./src/valve/steam:./src/valve/util
DESTPATH = ./bin

#SRCFILES = ./src/*.java ./src/*/*/*.java

compile:
	javac -cp .:$(SRCPATH):$(LIBPATH) -d $(DESTPATH) ./src/valve/util/*.java	
	javac -cp .:$(SRCPATH):$(LIBPATH) -d $(DESTPATH) ./src/valve/steam/*.java
	javac -cp .:$(SRCPATH):$(LIBPATH) -d $(DESTPATH) ./src/*.java	
run:
	java -cp .:$(SRCPATH):$(LIBPATH) Main
