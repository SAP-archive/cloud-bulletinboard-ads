REM This script prepares the current shell's environment variables (not permanently)

REM Used for backing services like the PostgreSQL database
SET VCAP_APPLICATION={}

SET VCAP_SERVICES={"postgresql-9.3":[{"name":"postgresql-lite","label":"postgresql-9.3","credentials":{"dbname":"test","hostname":"127.0.0.1","password":"test123!","port":"5432","uri":"postgres://testuser:test123!@localhost:5432/test","username":"testuser"},"tags":["relational","postgresql"],"plan":"free"}],"xsuaa":[{"credentials":{"clientid":"sb-clientId!t0815","clientsecret":"dummy-clientsecret","identityzone":"<<your tenant>>","identityzoneid":"a09a3440-1da8-4082-a89c-3cce186a9b6c","tenantid":"a09a3440-1da8-4082-a89c-3cce186a9b6c","uaadomain":"localhost","tenantmode":"shared","url":"dummy-url","verificationkey":"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm1QaZzMjtEfHdimrHP3/2Yr+1z685eiOUlwybRVG9i8wsgOUh+PUGuQL8hgulLZWXU5MbwBLTECAEMQbcRTNVTolkq4i67EP6JesHJIFADbK1Ni0KuMcPuiyOLvDKiDEMnYG1XP3X3WCNfsCVT9YoU+lWIrZr/ZsIvQri8jczr4RkynbTBsPaAOygPUlipqDrpadMO1momNCbea/o6GPn38LxEw609ItfgDGhL6f/yVid5pFzZQWb+9l6mCuJww0hnhO6gt6Rv98OWDty9G0frWAPyEfuIW9B+mR/2vGhyU9IbbWpvFXiy9RVbbsM538TCjd5JF2dJvxy24addC4oQIDAQAB-----END PUBLIC KEY-----","xsappname":"xsapp!t0815"},"label":"xsuaa","name":"uaa-bulletinboard","plan":"application","tags":["xsuaa"]}]}

REM Used for dependent service call
SET USER_ROUTE=https://opensapcp5userservice.cfapps.eu10.hana.ondemand.com

REM Overwrite logging library defaults
SET APPENDER=STDOUT
SET LOG_APP_LEVEL=TRACE
