all: compile run

LIBPATH = ./lib/ojdbc6.jar

DESTPATH = ./bin
CLASSPATH = ./bin:./bin/valve/steam:./bin/valve/util

compile:
	javac -cp .:$(LIBPATH) -d $(DESTPATH) ./src/*.java	./src/*/*/*.java
run:
	java -cp .:$(CLASSPATH):$(LIBPATH) Main
