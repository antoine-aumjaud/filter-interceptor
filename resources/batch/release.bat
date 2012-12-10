@echo off

setlocal

pushd %~dp0..\..

echo.
set /p PASSPHRASE="Enter the GPG passphrase: "
echo.
echo Run Maven Release in %cd%
mvn release:prepare release:perform -Darguments=-Dgpg.passphrase=%PASSPHRASE%

popd

endlocal

pause