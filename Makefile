#JAVA_HOME = /usr/lib/jvm/java-1.7.0-openjdk.x86_64/
JAVA_HOME = /usr/lib/jvm/default-java/

JC = javac
JAVA_SRCS = src/**/*.java src/*.java
JAVA_BIN = bin
JAVA_CP = $(wildcard lib/*.jar):.
JFLAGS = -cp $(JAVA_CP) -d $(JAVA_BIN)

JNI_CLASS = # TODO
JNI_HEADER = # TODO

CC = gcc
C_SRC = # TODO
C_LIB = # TODO
C_INCLUDE_DIRS = $(JAVA_HOME)/include $(JAVA_HOME)/include/linux $(dir $(JNI_HEADER))
CFLAGS = $(foreach d, $(C_INCLUDE_DIRS), -I $d) -std=c99 -shared -fPIC


all: $(C_LIB) java_classes

java_classes: $(JAVA_SRCS)
	mkdir -p $(JAVA_BIN) && $(JC) $(JFLAGS) $^

$(JNI_HEADER):
	javah -classpath ./src -o $(JNI_HEADER) $(JNI_CLASS)

$(C_LIB): $(C_SRC) $(JNI_HEADER)
	mkdir -p $(dir $@) && $(CC) $(CFLAGS) $< -o $@

tar:
	tar -czf isupeene-assignment1.tar.gz Makefile README.txt LICENSE.txt *.sh src/ lib/ doc/

clean:
	rm -rf include/ bin/
