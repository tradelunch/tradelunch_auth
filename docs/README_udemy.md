# Spring Security Basic
## Setup
```shell
git clone [ssh]
cd basic

# need to install java 21
/usr/libexec/java_home -v $(jenv version-name)
```

## How to run

build
```shell
# build
./gradlew clean build
```

run
```shell
# run
./gradlew bootRun
```


## config
```shell
# src/main/resources
application.yml # common
/dev
  applcation-dev.yml # dev
/prod
  application-prod.yml # prod
```

## Java run
dev
```shell
./gradlew build clean -Pprofile=dev
java -Dspring.profiles.active=dev,test -jar build/libs/basic-0.0.1-SNAPSHOT.jar
```

prod
```shell
./gradlew build clean -Pprofile=prod
java -Dspring.profiles.active=prod -jar build/libs/basic-0.0.1-SNAPSHOT.jar
```
