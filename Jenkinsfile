pipeline {
    agent {
        label 'docker-agent'
    }

    environment {
        COMPOSE_PROJECT_NAME = 'airsphereconnect'
        DOCKER_BUILDKIT = '1'
        // Déterminer l'environnement selon la branche
        ENV_FILE = "${env.BRANCH_NAME == 'main' ? '.env.prod' : '.env'}"
        DOCKER_COMPOSE_FILE = "${env.BRANCH_NAME == 'main' ? 'docker-compose.prod.yml' : 'docker-compose.dev.yml'}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=== Checkout from ${env.BRANCH_NAME} branch ==="
                checkout scm
                sh 'git rev-parse --short HEAD > .git/commit-id'
                script {
                    env.GIT_COMMIT_SHORT = readFile('.git/commit-id').trim()
                    echo "Git commit: ${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        stage('Environment Setup') {
            steps {
                echo "=== Environment Setup for ${env.BRANCH_NAME} ==="
                sh """
                    echo "Using environment file: ${ENV_FILE}"
                    echo "Using compose file: ${DOCKER_COMPOSE_FILE}"
                    if [ ! -f ${ENV_FILE} ]; then
                        echo "ERROR: ${ENV_FILE} not found!"
                        exit 1
                    fi
                """
            }
        }

        stage('Build Backend') {
            steps {
                dir('air-sphere-connect-back') {
                    echo '=== Build du backend Spring Boot ==='
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test Backend') {
            steps {
                dir('air-sphere-connect-back') {
                    echo '=== Tests unitaires backend ==='
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('air-sphere-connect-front') {
                    echo '=== Build du frontend Angular ==='
                    sh '''
                        npm ci
                        npm run build -- --configuration=${BRANCH_NAME == 'main' ? 'production' : 'development'}
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo "=== Construction des images Docker ==="
                sh '''
                    docker compose -f ${DOCKER_COMPOSE_FILE} build --no-cache
                '''
            }
        }

        stage('Stop Old Containers') {
            steps {
                echo '=== Arrêt des anciens containers ==='
                sh '''
                    docker compose -f ${DOCKER_COMPOSE_FILE} down --remove-orphans || true
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo "=== Déploiement vers ${env.BRANCH_NAME} ==="
                sh '''
                    docker compose -f ${DOCKER_COMPOSE_FILE} up -d --force-recreate
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Vérification de la santé des services ==='
                sh '''
                    sleep 30
                    docker compose -f ${DOCKER_COMPOSE_FILE} ps

                    # Vérifier que les containers sont up
                    if docker compose -f ${DOCKER_COMPOSE_FILE} ps | grep -q "unhealthy"; then
                        echo "WARNING: Some containers are unhealthy"
                        docker compose -f ${DOCKER_COMPOSE_FILE} logs --tail=50
                        exit 1
                    else
                        echo "All containers are healthy"
                    fi
                '''
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { return fileExists('sonar-project.properties') }
            }
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

        stage('Cleanup') {
            steps {
                echo '=== Nettoyage des ressources Docker ==='
                sh '''
                    docker system prune -f --volumes || true
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully for ${env.BRANCH_NAME} branch!"
            echo "Commit: ${env.GIT_COMMIT_SHORT}"
        }
        failure {
            echo "❌ Pipeline failed for ${env.BRANCH_NAME} branch!"
            sh "docker compose -f ${DOCKER_COMPOSE_FILE} logs --tail=100 || true"
        }
        always {
            echo '=== Cleaning up workspace ==='
            cleanWs()
        }
    }
}
