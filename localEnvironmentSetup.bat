REM This script prepares the current shell's environment variables (not permanently)

REM Used for backing services like the PostgreSQL database
SET VCAP_APPLICATION={}
SET VCAP_SERVICES={"postgresql-9.3":[{"name":"postgresql-lite","label":"postgresql-9.3","credentials":{"dbname":"test","hostname":"127.0.0.1","password":"test123!","port":"5432","uri":"postgres://testuser:test123!@localhost:5432/test","username":"testuser"},"tags":["relational","postgresql"],"plan":"free"}]}

REM Used for dependent service call
SET USER_ROUTE=https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com

REM Overwrite logging library defaults
SET APPENDER=STDOUT
SET LOG_APP_LEVEL=TRACE
