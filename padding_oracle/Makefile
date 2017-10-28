JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Decrypt.java \

default: all cleanup

all: decrypt encrypt

classes: $(CLASSES:.java=.class)

Decrypt.jar: Decrypt.class
	jar cfe Decrypt.jar Decrypt Decrypt.class

Encrypt.jar: Decrypt.class Encrypt.class
	jar cfe Encrypt.jar Encrypt Encrypt.class Decrypt.class

cleanup:
	$(RM) *.class *.jar temp_file

decrypt: Decrypt.jar
	cat stub.sh Decrypt.jar > decrypt && chmod +x decrypt

encrypt: Encrypt.jar
	cat stub.sh Encrypt.jar > encrypt && chmod +x encrypt

clean: cleanup
	$(RM) decrypt encrypt
