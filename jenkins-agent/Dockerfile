FROM jenkins/inbound-agent:latest-jdk21

USER root

# Install Node.js, Docker and other tools
RUN apt-get update && apt-get install -y \
    curl \
    docker.io \
    wget \
    gnupg \
    && apt-get clean

# Install Docker Compose from GitHub
RUN curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose

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

# Add jenkins user to docker group with GID 988 (matches host)
RUN groupadd -f -g 988 docker && usermod -aG docker jenkins

# Verify installations
RUN java -version && mvn -v && node -v && npm -v && docker --version && docker-compose --version

USER jenkins