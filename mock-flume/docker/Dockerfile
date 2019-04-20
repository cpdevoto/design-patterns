FROM openjdk:8u121-jdk-alpine

# This is 
# This docker file installs JRE8 on Alpine, then the Mock Flume application
#

RUN set -x \
    && apk --update add \
       bash \
       curl \
       wget \
       jq \
    && rm -rf /var/cache/apk/*

COPY build/flume/ /opt/
COPY build/version.txt docker/entrypoint.sh /opt/apache-flume/

WORKDIR /opt/apache-flume

CMD ["./entrypoint.sh"]

