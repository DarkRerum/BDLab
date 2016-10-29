all: compile package

LIBPATH = ./lib/ojdbc6.jar

DESTPATH = ./bin
CLASSPATH = ./bin:./bin/valve/steam:./bin/valve/util

compile:
	javac -cp .:$(LIBPATH) -d $(DESTPATH) ./src/*.java	./src/*/*/*.java
package:
	jar cfm steam.jar ./src/MANIFEST.MF -C ./bin .
run:
	java -jar steam.jar
	#alias steam='java -jar steam.jar'
	#java -cp .:$(CLASSPATH):$(LIBPATH) Main
