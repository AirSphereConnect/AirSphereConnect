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
