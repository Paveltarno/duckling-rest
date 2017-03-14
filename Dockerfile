FROM clojure:alpine
MAINTAINER Pavel Tarno <paveltarno@gmail.com>

COPY . /duckling-server
WORKDIR /duckling-server

RUN apk update && apk upgrade && \
    apk add --no-cache git openssh
RUN git clone --depth 1 https://github.com/Paveltarno/duckling.git

WORKDIR /duckling-server/duckling
RUN lein jar
RUN lein install

WORKDIR /duckling-server
RUN lein uberjar

EXPOSE 9000

ENTRYPOINT ["java"]
CMD ["-Xms256m", "-Xmx512m", "-Djava.awt.headless=true", "-jar", "target/duckling-rest-0.1.1-SNAPSHOT-standalone.jar"]