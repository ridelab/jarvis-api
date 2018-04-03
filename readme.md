# onenet-rc

## usage

firstly modify configuration in `conf/application.conf`

### development

```bash
sbt run
ngrok http 5600
```

### deployment

```bash
sbt dist
cd target/universal
unzip onenet-rc-*.zip
cd onenet-rc-*
nohup bin/onenet-rc -Dplay.http.secret.key=$SECRET -Dhttp.port=$PORT &
# kill $(cat RUNNING_PID)
```

## references

- <https://open.iot.10086.cn/doc/art283.html#68>
- <https://www.playframework.com/documentation>
