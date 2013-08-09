call mvn package
call adb uninstall "com.commafeed.newsplus"
call adb install target/commafeed-newsplus.apk