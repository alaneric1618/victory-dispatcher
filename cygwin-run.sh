#!/bin/bash
mkdir bin/ 2> /dev/null
files=""
for file in `ls ./src`; do
    if [[ $file =~ Test ]]; then
	: #NO-OP
    else
        files=$files" ./src/"$file
    fi
done
javac -cp ./bin/ -cp rsyntaxtextarea-2.5.8.jar -sourcepath rsyntaxtextarea-2.5.8.jar -sourcepath autocomplete-2.5.8.jar -d ./bin $files
java -cp "./bin/;./rsyntaxtextarea-2.5.8.jar" VD
