FROM maven:3.8.6-openjdk-11 as builder

# Install any necessary tools and updates
RUN apt-get update && apt-get install -y \
    unattended-upgrades \
    apt-listchanges && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory and copy project files into Docker image
WORKDIR /usr/src/app
COPY . /usr/src/app

# Run Maven to build the project
RUN mvn clean package

# Use Tomcat base image
FROM tomcat:9-jdk11-corretto

# Copy the built WAR file from the builder stage
COPY --from=builder /usr/src/app/target/*.war /usr/local/tomcat/webapps/

# Copy the configuration and other necessary files
COPY *.json /usr/local/tomcat/
COPY gmd2geodcat.xsl /usr/local/tomcat/
COPY dcat-ap-rdf2rdfa.xsl /usr/local/tomcat/
COPY users.xml /usr/local/tomcat/
COPY prefixes.txt /usr/local/tomcat/
COPY htmlcovtemplate.txt /usr/local/tomcat/
COPY htmltemplate.txt /usr/local/tomcat/
COPY htmltemplate2.txt /usr/local/tomcat/