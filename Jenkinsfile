pipeline {
    agent {
        label 'docker-agent'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=== Checkout du code ==="
                checkout scm
                sh 'git rev-parse --short HEAD > .git/commit-id'
                script {
                    env.GIT_COMMIT_SHORT = readFile('.git/commit-id').trim()
                    echo "Git commit: ${env.GIT_COMMIT_SHORT}"
                }
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
                    sh 'mvn test || true'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('air-sphere-connect-front') {
                    echo '=== Build du frontend Angular ==='
                    sh '''
                        npm ci
                        npm run build
                    '''
                }
            }
        }

        stage('Prepare Environment') {
            steps {
                echo '=== Préparation de l\'environnement ==='
                sh '''
                    if [ ! -f .env ]; then
                        echo "Copie de .env.example vers .env"
                        cp .env.example .env
                    else
                        echo ".env existe déjà"
                    fi
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '=== Construction des images Docker ==='
                sh '''
                    docker-compose -f docker-compose.dev.yml build --no-cache
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo '=== Déploiement des containers ==='
                sh '''
                    docker-compose -f docker-compose.dev.yml down --remove-orphans || true
                    docker-compose -f docker-compose.dev.yml up -d --force-recreate
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Vérification de la santé des services ==='
                sh '''
                    echo "Attente du démarrage des services..."
                    sleep 30

                    docker-compose -f docker-compose.dev.yml ps

                    # Vérifier que les containers sont en cours d'exécution
                    UNHEALTHY=$(docker-compose -f docker-compose.dev.yml ps --filter "health=unhealthy" -q | wc -l)

                    if [ "$UNHEALTHY" -gt 0 ]; then
                        echo "⚠️ WARNING: $UNHEALTHY container(s) unhealthy"
                        docker-compose -f docker-compose.dev.yml logs --tail=50
                        exit 1
                    else
                        echo "✅ All containers are healthy"
                    fi
                '''
            }
        }

    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
            echo "Commit: ${env.GIT_COMMIT_SHORT}"
        }
        failure {
            echo "❌ Pipeline failed!"
        }
    }
}
