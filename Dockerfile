# Custom image for Tomcat 9.0.74 on JDK 19
FROM maven:3.8.7-eclipse-temurin-19

# create tomcat folder
RUN mkdir /opt/tomcat/
WORKDIR /opt/tomcat

# download tomcat
RUN curl -O https://downloads.apache.org/tomcat/tomcat-9/v9.0.74/bin/apache-tomcat-9.0.74.tar.gz
RUN tar xvfz apache*.tar.gz
RUN mv apache-tomcat-9.0.74/* /opt/tomcat/.

# download sample app
WORKDIR /opt/tomcat/webapps
RUN curl -O https://tomcat.apache.org/tomcat-9.0-doc/appdev/sample/sample.war

# start tomcat
EXPOSE 8080
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
