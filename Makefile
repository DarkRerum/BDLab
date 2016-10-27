all: compile run

LIBPATH = ./lib/ojdbc6.jar

SRCFILES = ./src/*.java

compile:
	javac -cp .:./src:$(LIBPATH) $(SRCFILES)

run:
	java -cp .:./src:$(LIBPATH) Main
