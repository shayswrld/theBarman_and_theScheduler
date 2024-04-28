#Makefile with multiple files

JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin

$(BINDIR)/%.class: $(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<

CLASSES = barScheduling/DrinkOrder.class barScheduling/Barman.class barScheduling/Patron.class barScheduling/SchedulingSimulation.class

CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/barScheduling/*.class

run: $(CLASS_FILES)
	$(JAVA) -cp $(BINDIR):. barScheduling/SchedulingSimulation $(ARGS)
