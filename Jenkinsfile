pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKER_IMAGE = 'hannoi/hyundai_autoever_itstudy_jeonghan'
        DOCKER_CONFIG = "${WORKSPACE}/.docker"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean test --no-daemon'
            }
        }

        stage('Build image') {
            steps {
                script {
                    def shortCommit = sh(
                        script: 'git rev-parse --short=8 HEAD',
                        returnStdout: true
                    ).trim()
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortCommit}"
                }
                sh '''
                    docker build --pull \
                      --tag "$DOCKER_IMAGE:$IMAGE_TAG" \
                      --tag "$DOCKER_IMAGE:latest" \
                      .
                '''
            }
        }

        stage('Push image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    sh '''
                        set +x
                        mkdir -p "$DOCKER_CONFIG"
                        printf '%s' "$DOCKERHUB_TOKEN" | \
                          docker login --username "$DOCKERHUB_USERNAME" --password-stdin
                        docker push "$DOCKER_IMAGE:$IMAGE_TAG"
                        docker push "$DOCKER_IMAGE:latest"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pushed ${DOCKER_IMAGE}:${IMAGE_TAG} and ${DOCKER_IMAGE}:latest"
        }
        always {
            sh 'rm -rf "$DOCKER_CONFIG"'
        }
    }
}
