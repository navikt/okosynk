SETLOCAL EnableExtensions

SET this.dir=%~dp0

rd /Q /s %this.dir%cli\batch
rd /Q /s %this.dir%cli\logs
rd /Q /s %this.dir%batch\bokosynk001
rd /Q /s %this.dir%batch\bokosynk002
rd /Q /s %this.dir%batch\ukjent-batchnavn
rd /Q /s %this.dir%logs

