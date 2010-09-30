@echo off
cd .. 
cd ..
"%JAVA_HOME%"\bin\xjc.exe -d src resources/settings.xsd
pause