MAINCLASS=App

SRCPATH=src

JARPATH=../jars

DSTPATH=classes

CLASSPATH=$(subst $(eval) ,:,$(wildcard $(JARPATH)/*.jar))

all:
        mkdir -p classes
        javac \
                -sourcepath $(SRCPATH) \
                -classpath $(SRCPATH):$(CLASSPATH) \
                $(SRCPATH)/$(MAINCLASS).java \
                -d $(DSTPATH) \

        echo "#!/bin/bash" > run.sh
        echo java -cp $(DSTPATH):$(CLASSPATH) $(MAINCLASS) \"'$$@'\" >> run.sh
        chmod +x run.sh
