@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  processing startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and PROCESSING_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\processing-1.0-beta.jar;%APP_HOME%\lib\core-3.3.6.jar;%APP_HOME%\lib\gluegen-rt-main-2.3.2.jar;%APP_HOME%\lib\jogl-2.3.2.jar;%APP_HOME%\lib\jogl-all-2.3.2.jar;%APP_HOME%\lib\newt-main-2.3.2.jar;%APP_HOME%\lib\nativewindow-main-2.3.2.jar;%APP_HOME%\lib\gluegen-rt-2.3.2.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\gluegen-rt-2.3.2-natives-windows-i586.jar;%APP_HOME%\lib\newt-2.3.2.jar;%APP_HOME%\lib\newt-2.3.2-event.jar;%APP_HOME%\lib\newt-2.3.2-ogl.jar;%APP_HOME%\lib\newt-2.3.2-driver-android.jar;%APP_HOME%\lib\newt-2.3.2-driver-bcm-old.jar;%APP_HOME%\lib\newt-2.3.2-driver-bcm-vc.jar;%APP_HOME%\lib\newt-2.3.2-driver-intelgdl.jar;%APP_HOME%\lib\newt-2.3.2-driver-kd.jar;%APP_HOME%\lib\newt-2.3.2-driver-linux.jar;%APP_HOME%\lib\newt-2.3.2-driver-osx.jar;%APP_HOME%\lib\newt-2.3.2-driver-win.jar;%APP_HOME%\lib\newt-2.3.2-driver-x11.jar;%APP_HOME%\lib\newt-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\newt-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\newt-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\newt-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\newt-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\newt-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\newt-2.3.2-natives-windows-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-win.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-osx.jar;%APP_HOME%\lib\nativewindow-2.3.2-os-x11.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-android-aarch64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-android-armv6.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-armv6.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-armv6hf.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-linux-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-macosx-universal.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-solaris-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-solaris-i586.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-windows-amd64.jar;%APP_HOME%\lib\nativewindow-2.3.2-natives-windows-i586.jar

@rem Execute processing
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PROCESSING_OPTS%  -classpath "%CLASSPATH%" main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PROCESSING_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PROCESSING_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
