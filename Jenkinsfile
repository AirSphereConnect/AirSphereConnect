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
                sh '''
                    echo "Copie forcée de .env.example.prod vers .env pour CI/CD"
                    cp -f .env.example.prod .env

                    echo "Vérification de la configuration:"
                    echo "DB_PORT:"
                    grep DB_PORT .env
                    echo "DB_USER:"
                    grep DB_USER .env
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '=== Construction des images Docker ==='
                script {
                    // Utiliser prod.yml pour main et jenkins (CI/CD), dev.yml pour les autres branches
                    def composeFile = 'docker-compose.prod.yml'
                    sh """
                        docker-compose -p airsphereconnect -f ${composeFile} build
                    """
                }
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
                        // Toujours utiliser prod.yml pour CI/CD
                        def composeFile = 'docker-compose.prod.yml'
                        def environment = branchName == 'main' ? 'PRODUCTION' : 'STAGING (jenkins branch)'

                        echo "Déploiement en ${environment}"

                        sh """
                            docker-compose -p airsphereconnect -f ${composeFile} up -d --force-recreate --remove-orphans
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
                        // Toujours utiliser prod.yml pour CI/CD
                        def composeFile = 'docker-compose.prod.yml'

                        sh """
                            echo "Attente du démarrage des services..."
                            sleep 30

                            docker-compose -p airsphereconnect -f ${composeFile} ps

                            # Vérifier que les containers sont en cours d'exécution
                            UNHEALTHY=\$(docker-compose -p airsphereconnect -f ${composeFile} ps --filter "health=unhealthy" -q | wc -l)

                            if [ "\$UNHEALTHY" -gt 0 ]; then
                                echo "⚠️ WARNING: \$UNHEALTHY container(s) unhealthy"
                                docker-compose -p airsphereconnect -f ${composeFile} logs --tail=50
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
