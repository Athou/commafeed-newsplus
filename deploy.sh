#!/bin/sh

mvn package
adb install -r target/commafeed-newsplus.apk