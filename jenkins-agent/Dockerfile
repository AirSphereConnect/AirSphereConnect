FROM jenkins/inbound-agent:latest

USER root

RUN apt-get update && apt-get install -y \
    maven \
    nodejs \
    npm \
    docker.io \
    curl \
    && apt-get clean

RUN npm install -g npm@latest

RUN mvn -v && node -v && npm -v && docker --version

USER jenkins