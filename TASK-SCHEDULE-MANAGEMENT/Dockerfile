FROM openjdk:8

ARG version=1.0.0

WORKDIR /server

ENV LANG=C.UTF-8 TZ=Asia/Shanghai

COPY build/libs/lib lib

COPY build/libs/tsm-${version}.jar tsm.jar

CMD java -jar tsm.jar