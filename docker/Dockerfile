FROM openjdk:alpine
MAINTAINER <swissqr@gmail.com>
RUN mkdir -p /usr/local/bin/swissqr
WORKDIR /usr/local/bin/swissqr
COPY *.* /usr/local/bin/swissqr/
EXPOSE 9990
VOLUME /usr/local/bin/SmartEdgar/data
CMD ["java", "-Xmx600m", "-server", "-Dlog4j.configuration=file:log4j.properties", "-cp", "*", "ch.swissqr.service.web.Main"]
 

