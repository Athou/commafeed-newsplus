#!/bin/sh

mvn package
adb uninstall "com.commafeed.newsplus"
adb install target/commafeed-newsplus.apk