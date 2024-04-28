#Makefile with multiple files

JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<

CLASSES = clubSimulation/GridBlock.class clubSimulation/PeopleCounter.class clubSimulation/CounterDisplay.class\
		  clubSimulation/PeopleLocation.class clubSimulation/ClubGrid.class clubSimulation/ClubView.class \
		  clubSimulation/Clubgoer.class clubSimulation/AndreBarman.class clubSimulation/ClubSimulation.class 

CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/clubSimulation/*.class

run: $(CLASS_FILES)
	$(JAVA) -cp $(BINDIR):. clubSimulation/ClubSimulation $(ARGS)
