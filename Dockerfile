# Use an official Tomcat base image
FROM tomcat:9.0

# Set the working directory
WORKDIR /usr/local/tomcat/webapps/

# Copy the custom server.xml to replace the default configuration
COPY conf/server.xml /usr/local/tomcat/conf/server.xml
COPY conf/rewrite.config /usr/local/tomcat/conf/Catalina/localhost/rewrite.config

# Copy the WAR file to the Tomcat webapps directory
COPY target/vulnerable-xxe-app.war /usr/local/tomcat/webapps/

# Expose port 8080 to access the web application
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]
