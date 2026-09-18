pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKER_IMAGE = 'hannoi/hyundai_autoever_itstudy_jeonghan'
        IMAGE_TAG = 'consumer-1.0'
        DOCKER_PLATFORM = 'linux/amd64'
        DOCKER_BUILDKIT = '1'
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
                // Dockerfile의 JDK 17 빌드 단계에서 테스트까지 실행한다.
                sh 'docker build --pull --target build .'
            }
        }

        stage('Build image') {
            steps {
                sh 'docker build --platform "$DOCKER_PLATFORM" --pull --tag "$DOCKER_IMAGE:$IMAGE_TAG" .'
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
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pushed ${DOCKER_IMAGE}:${IMAGE_TAG}"
        }
        always {
            sh 'rm -rf "$DOCKER_CONFIG"'
        }
    }
}
