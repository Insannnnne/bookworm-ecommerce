@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET ___MVNW_UGLY_EXCEPTION_FIXED=0
@SETLOCAL
@SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBaseDir

@SET EXEC_DIR=%CD%
@SET WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn GOTO baseDirFound
cd ..
IF "%WDIR%"=="%CD%" GOTO baseDirNotFound
SET WDIR=%CD%
GOTO findBaseDir

:baseDirFound
SET MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
GOTO endDetectBaseDir

:baseDirNotFound
SET MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
  IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\MavenWrapperDownloader.java" (
    %WRAPPER_JAR_NOT_FOUND_WARNING%
  )
)

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@SET M2_HOME=
@SET JAVA_HOME_TO_USE=%JAVA_HOME%
@IF "%JAVA_HOME_TO_USE%"=="" (
  FOR /f "tokens=*" %%a IN ('where java 2^>NUL') DO (
    SET JAVA_HOME_TO_USE=%%~dpa..
    GOTO :foundJava
  )
)
:foundJava

IF NOT EXIST "%WRAPPER_JAR%" (
    %JAVA_HOME_TO_USE%\bin\java -cp "" ^
        "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
        "-Dmaven.wrapper.launcherScript=%MAVEN_PROJECTBASEDIR%\mvnw.cmd" ^
        -jar "%DOWNLOAD_URL%" ^
        -- download "%WRAPPER_JAR%" 2>NUL
    IF NOT EXIST "%WRAPPER_JAR%" (
        echo Downloading Maven Wrapper...
        powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%WRAPPER_JAR%'" 2>NUL
    )
)

@"%JAVA_HOME_TO_USE%\bin\java" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
@IF ERRORLEVEL 1 GOTO error
@GOTO end

:error
SET ERROR_CODE=%ERRORLEVEL%

:end
@ENDLOCAL & SET ERROR_CODE=%ERROR_CODE%

@CMD /C EXIT /B %ERROR_CODE%
