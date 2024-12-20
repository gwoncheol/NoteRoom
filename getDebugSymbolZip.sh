#!/bin/sh
ORIGINAL_DIR=$(pwd)

cd ./app/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib

if [ -e debugSymbols.zip ]; then
    rm debugSymbols.zip
fi

zip -r debugSymbols.zip . -x "*.DS_Store"

zip -d debugSymbols.zip "__MACOSX"

mv debugSymbols.zip $ORIGINAL_DIR/app/release/debugSymbols.zip

cd $ORIGINAL_DIR