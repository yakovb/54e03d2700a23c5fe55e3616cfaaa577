FROM alpine/java:21-jdk

WORKDIR /app

COPY . /app
RUN apk add --no-cache maven
RUN mvn install -DskipTests