JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Decrypt.java \

default: decrypt cleanup

classes: $(CLASSES:.java=.class)

decrypt.jar: Decrypt.class
	jar cfe decrypt.jar Decrypt Decrypt.class

cleanup:
	$(RM) *.class *.jar

decrypt: decrypt.jar
	cat stub.sh decrypt.jar > decrypt && chmod +x decrypt

clean: cleanup
	$(RM) decrypt
