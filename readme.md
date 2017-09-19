# jarvis-api

<!--
[![Build Status][build-badge]][build-status]
[![Test Coverage][coverage-badge]][coverage-result]
-->

## usage

firstly modify configuration in `conf/application.conf`

### development

```bash
sbt run
ngrok http 9000
```

### deployment

```bash
sbt dist
cd target/universal
unzip jarvis-api-*.zip
cd jarvis-api-*
nohup bin/jarvis-api -Dplay.http.secret.key=$SECRET -Dhttp.port=$PORT &
# kill $(cat RUNNING_PID)
```

## references

- <https://www.playframework.com/documentation>
- <https://www.playframework.com/documentation/latest/Deploying>
- <https://www.playframework.com/documentation/latest/ProductionConfiguration>

[build-badge]: https://img.shields.io/travis/airt/jarvis-api.svg
[build-status]: https://travis-ci.org/airt/jarvis-api
[coverage-badge]: https://img.shields.io/coveralls/airt/jarvis-api.svg
[coverage-result]: https://coveralls.io/github/airt/jarvis-api
