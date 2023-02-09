@echo off

call mvnw clean package -Dmaven.test.skip=true

pause 