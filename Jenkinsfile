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
                    echo "Branch name: ${env.BRANCH_NAME}"
                    echo "Git branch: ${env.GIT_BRANCH}"
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
                script {
                    // Détecter la branche
                    def currentBranch = env.GIT_BRANCH ?: env.BRANCH_NAME ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    def branchName = currentBranch.replaceAll(/^origin\//, '')

                    // Copier .env.prod depuis le serveur pour le déploiement
                    sh '''
                        echo "=== Copie de .env.prod depuis /var/www/projects/airsphereconnect ==="
                        if [ -f /var/www/projects/airsphereconnect/.env.prod ]; then
                            cp -f /var/www/projects/airsphereconnect/.env.prod .env.prod
                            echo "✅ .env.prod copié avec succès"
                        else
                            echo "⚠️ .env.prod non trouvé sur le serveur, utilisation de .env.example.prod"
                            cp -f .env.example.prod .env.prod
                        fi
                    '''

                    if (branchName == 'main') {
                        sh '''
                            echo "=== PRODUCTION: Copie de .env.prod vers .env ==="
                            cp -f .env.prod .env
                        '''
                    } else {
                        sh '''
                            echo "=== DEVELOPMENT: Utilisation du .env existant ou copie de .env.example ==="
                            if [ ! -f .env ]; then
                                echo "Copie de .env.example vers .env"
                                cp .env.example .env
                            else
                                echo ".env existe déjà, conservation"
                            fi
                        '''
                    }

                    sh '''
                        echo "Vérification de la configuration .env.prod:"
                        grep JWT_SECRET .env.prod || echo "⚠️ JWT_SECRET non trouvé"
                        grep DB_ROOT_PASSWORD .env.prod || echo "⚠️ DB_ROOT_PASSWORD non trouvé"
                    '''
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '=== Construction des images Docker ==='
                sh """
                    docker-compose -f docker-compose.prod.yml build
                """
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Détecter la branche actuelle de manière robuste
                    def currentBranch = env.GIT_BRANCH ?: env.BRANCH_NAME ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()

                    // Nettoyer le nom de branche (enlever origin/ si présent)
                    def branchName = currentBranch.replaceAll(/^origin\//, '')

                    echo "Branche détectée: ${branchName}"

                    if (branchName == 'main' || branchName == 'jenkins') {
                        echo '=== Déploiement des containers ==='
                        def environment = branchName == 'main' ? 'PRODUCTION' : 'STAGING'

                        echo "Déploiement en ${environment} avec docker-compose.prod.yml"

                        sh """
                            # Arrêter les containers de l'application s'ils existent
                            docker stop air_sphere_connect_back air_sphere_connect_front air_sphere_connect_db 2>/dev/null || true
                            docker rm air_sphere_connect_back air_sphere_connect_front air_sphere_connect_db 2>/dev/null || true

                            # Démarrer avec docker-compose
                            docker-compose -f docker-compose.prod.yml up -d --force-recreate
                        """
                    } else {
                        echo "⏭️ Déploiement ignoré pour la branche '${branchName}' (déploiement uniquement sur 'main' et 'jenkins')"
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    // Détecter la branche actuelle de manière robuste
                    def currentBranch = env.GIT_BRANCH ?: env.BRANCH_NAME ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()

                    // Nettoyer le nom de branche (enlever origin/ si présent)
                    def branchName = currentBranch.replaceAll(/^origin\//, '')

                    if (branchName == 'main' || branchName == 'jenkins') {
                        echo '=== Vérification de la santé des services ==='

                        sh """
                            echo "Attente du démarrage des services..."
                            sleep 30

                            docker-compose -f docker-compose.prod.yml ps

                            # Vérifier que les containers sont en cours d'exécution
                            UNHEALTHY=\$(docker-compose -f docker-compose.prod.yml ps --filter "health=unhealthy" -q | wc -l)

                            if [ "\$UNHEALTHY" -gt 0 ]; then
                                echo "⚠️ WARNING: \$UNHEALTHY container(s) unhealthy"
                                docker-compose -f docker-compose.prod.yml logs --tail=50
                                exit 1
                            else
                                echo "✅ All containers are healthy"
                            fi
                        """
                    } else {
                        echo "⏭️ Health check ignoré pour la branche '${branchName}' (déploiement uniquement sur 'main' et 'jenkins')"
                    }
                }
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
