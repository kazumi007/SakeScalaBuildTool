::#!
@echo off
rem Warning: untested!
if "%SAKE_HOME%" == "" (
  set _SAKE_TMP=%~dp0
  set SAKE_HOME=%_SAKE_TMP%\..\
)

set SAKE_CLASSPATH=
for %%f in ("%SAKE_HOME%\lib\*") do call :add_cpath "%%f"

call scala -classpath %SAKE_CLASSPATH%

goto :eof

::!#
:load sake.scala
build("%*")

:add_cpath
  if "%SAKE_CLASSPATH%"=="" (
    set SAKE_CLASSPATH=%~1
  ) else (
    set SAKE_CLASSPATH=%SAKE_CLASSPATH%;%~1
  )
goto :eof