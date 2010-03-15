@echo on
rem Warning: untested!
if "%SAKE_HOME%" == "" (
  set SAKE_HOME=%~dp0..\
)

set SAKE_CLASSPATH=
for %%f in ("%SAKE_HOME%\lib\*") do call :add_cpath "%%f"

call scala -classpath %SAKE_CLASSPATH% -i sake.scala

goto :eof

:add_cpath
  if "%SAKE_CLASSPATH%"=="" (
    set SAKE_CLASSPATH=%~1
  ) else (
    set SAKE_CLASSPATH=%SAKE_CLASSPATH%;%~1
  )
goto :eof
