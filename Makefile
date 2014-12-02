empty =
space = $(empty) $(empty)

#JAVA_HOME = /usr/lib/jvm/java-1.7.0-openjdk.x86_64/
JAVA_HOME = /usr/lib/jvm/default-java/

JC = javac
JAVA_SRCS = src/**/*.java src/*.java
JAVA_BIN = bin
JAVA_CP = $(wildcard lib/*.jar) .
JFLAGS = -cp $(subst $(space),:,$(JAVA_CP)) -d $(JAVA_BIN)

JNI_CLASS = encryption.FeistelCipher
JNI_HEADER = include/encryption/encryption.h

CC = gcc
C_SRC = src/encryption/encryption.c
C_LIB = bin/encryption/libencryption.so
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
	tar -czf isupeene-assignment2.tar.gz Makefile README.md LICENSE *.sh src/ lib/ doc/ config/

clean:
	rm -rf include/ bin/
