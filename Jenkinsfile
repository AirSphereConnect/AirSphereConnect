pipeline {
    // 🧩 Utilisation d'un agent "docker" si tu en as un, sinon "any"
    agent any

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
            steps {
                dir('air-sphere-connect-back') {
                    sh '''
                        echo "=== Build du backend Spring Boot ==="
                        mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('air-sphere-connect-front') {
                    sh '''
                        echo "=== Build du frontend Angular ==="
                        npm install
                        npm run build
                    '''
                }
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                sh '''
                    echo "=== (Re)construction et déploiement Docker Compose ==="
                    docker compose -f ${DOCKER_COMPOSE_FILE} down || true
                    docker compose -f ${DOCKER_COMPOSE_FILE} build --no-cache
                    docker compose -f ${DOCKER_COMPOSE_FILE} up -d
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "=== Analyse SonarQube ==="
                    withSonarQubeEnv('SonarQube') {
                        dir('air-sphere-connect-back') {
                            sh 'mvn sonar:sonar'
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                sh '''
                    echo "=== Vérification de la santé des services ==="
                    curl -f http://localhost:8080/actuator/health || exit 1
                    curl -f http://localhost:4200 || exit 1
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Échec du pipeline.'
        }
    }
}
