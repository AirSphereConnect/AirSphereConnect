pipeline {
    agent { label 'docker' }

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.dev.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'dev-front', url: 'https://github.com/AirSphereConnect/AirSphereConnect.git'
            }
        }

        stage('Build Backend') {
            dir('air_sphere_connect_back'){
                sh 'mvn clean package -DskipTests' 
            }
        }

        stage('Build Frontend') {
            dir('air_sphere_connect_front') {
                sh '''
                cd air-sphere-connect-front
                npm install
                npm run build
                '''
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                sh """
                docker-compose -f ${DOCKER_COMPOSE_FILE} down
                docker-compose -f ${DOCKER_COMPOSE_FILE} build
                docker-compose -f ${DOCKER_COMPOSE_FILE} up -d
                """
            }
        }
      
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
      
        stage('Tests Integration') {
            steps {
                sh '''
                curl -f http://localhost:8080/actuator/health
                curl -f http://localhost:4200
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline terminé avec succès!'
        }
        failure {
            echo 'Échec du pipeline.'
        }
    }
}
