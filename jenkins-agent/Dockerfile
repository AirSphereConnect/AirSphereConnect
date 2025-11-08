FROM jenkins/inbound-agent:latest-jdk21

USER root

# Install Node.js, Docker and other tools
RUN apt-get update && apt-get install -y \
    curl \
    docker.io \
    wget \
    gnupg \
    && apt-get clean

# Install Node.js 20 from NodeSource
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

# Install Maven 3.9.x
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    && tar -xzf apache-maven-3.9.6-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.9.6 /opt/maven \
    && rm apache-maven-3.9.6-bin.tar.gz

ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

# Verify installations
RUN java -version && mvn -v && node -v && npm -v && docker --version

USER jenkins