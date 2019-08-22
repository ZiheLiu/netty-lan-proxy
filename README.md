# Netty LAN Proxy

[![Build Status](https://travis-ci.org/ZiheLiu/JavaMavenTeamplate.svg?branch=master)](https://travis-ci.org/ZiheLiu/JavaMavenTeamplate)

Proxy server and client for Local Area Network (LAN) using netty.



## Dependencies

- `Maven` > 3.0
- `Java` > 8.0


## TODO

- [ ] Use `Junit` to test.
- [ ] Use `travis` to build after push to GitHub.


## Usages

### 生成SSL证书

```shell
# 生成server私钥和证书仓库
$ keytool -genkey -alias netty-lan-proxy-server -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass sNetty -storepass sNetty -keystore server.jks

# 生成server自签名证书
$ keytool -export -alias netty-lan-proxy-server -keystore server.jks -storepass sNetty -file server.cer


# 生成client密钥对和证书仓库
$ keytool -genkey -alias netty-lan-proxy-client -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass cNetty -storepass cNetty -keystore client.jks

# 生成client签名证书
$ keytool -export -alias netty-lan-proxy-client -keystore client.jks -storepass cNetty -file client.cer


# 将server的证书导入到client的证书仓库中
$keytool -import -alias netty-lan-proxy-server -trustcacerts -file server.cer -storepass cNetty -keystore client-trust.jks

# 将client的证书导入到server的证书仓库中
keytool -import -alias netty-lan-proxy-client -trustcacerts -file client.cer -storepass sNetty -keystore server-trust.jks  
```

### 运行
```shell
$ mvn clean test

$ mvn clean package
```
