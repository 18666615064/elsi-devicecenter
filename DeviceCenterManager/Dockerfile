FROM java:8-jre-alpine
VOLUME /tmp
ADD /target/*.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 9797
EXPOSE 8807
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-DLOGGER_HOME=/usr/log","-Djava.security.egd=file:/dev/./urandom","-Xdebug","-Xnoagent","-Djava.compiler=NONE","-Xrunjdwp:transport=dt_socket,suspend=n,address=9797,server=y","-jar","/app.jar"]