@echo off

call mvn clean package spring-boot:repackage -Dmaven.test.skip=true
pause 