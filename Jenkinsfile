pipeline {
    agent any

    environment {
        registryCredential = 'docker-hub' // Docker Hub에 로그인할 때 사용할 자격 증명 ID
        dockerImage = '' // Docker 이미지 변수 초기화
        manifest = '' // YAML 파일 변수 초기화
    }

    stages {
        stage('Setup Credentials') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'docker-hub-username', variable: 'DOCKER_HUB_USERNAME'),
                                     string(credentialsId: 'member-image-name', variable: 'MEMBER_IMAGE_NAME'),
                                     string(credentialsId: 'kube-master-username', variable: 'KUBE_MASTER_USERNAME'),
                                     string(credentialsId: 'kube-master-ip', variable: 'KUBE_MASTER_IP'),
                                     file(credentialsId: 'member-service-yaml', variable: 'MANIFEST')]) { // YAML 파일 가져오기
                        // 환경 변수 설정
                        env.dockerHubUsername = DOCKER_HUB_USERNAME
                        env.memberImageName = MEMBER_IMAGE_NAME
                        env.kubeMasterNodeServerUsername = KUBE_MASTER_USERNAME
                        env.kubeMasterNodeServerIp = KUBE_MASTER_IP
                        env.fullImageName = "${env.dockerHubUsername}/${env.memberImageName}" // fullImageName 설정
                        env.manifest = MANIFEST // manifest 파일 경로 설정
                    }
                }
            }
        }

        stage('Cloning Repository') {
            steps {
                echo 'Cloning Repository'
                git url: 'https://github.com/KEA-5th-Myaong/member-service.git',
                    branch: 'develop',
                    credentialsId: 'github-token'
            }
            post {
                success {
                    echo 'Successfully Cloned Repository'
                }
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Secret File Download') {
            steps {
                withCredentials([file(credentialsId: 'member-application.yml', variable: 'application')]) {
                    dir('.') {
                        script {
                            if (!fileExists("src/main/resources/")) {
                                sh "mkdir -p src/main/resources/"
                            }
                            sh "cp \$application src/main/resources/"
                        }
                    }
                }
            }
        }

        stage('Build Gradle') {
            steps {
                echo 'Building with Gradle'
                dir('.') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build -x test'
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Build Docker') {
            steps {
                echo 'Building Docker Image'
                script {
                    dockerImage = docker.build("${env.fullImageName}:${env.BUILD_ID}")
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Push Docker') {
            steps {
                echo 'Pushing Docker Image'
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Container Stop') {
            steps {
                echo 'Stopping Previous Container'
                sshagent (credentials: ['kube-master-ssh']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ${kubeMasterNodeServerUsername}@${kubeMasterNodeServerIp} 'docker ps -q --filter name=${env.memberImageName} | xargs -r docker stop || true'
                    """
                }
            }
        }

        stage('Image Delete on Kubernetes') {
            steps {
                echo 'Removing Previous Docker Image on Kubernetes'
                script {
                    def previousBuildId = "${env.BUILD_ID.toInteger() - 1}"
                    sshagent (credentials: ['kube-master-ssh']) {
                        sh """
                        ssh -o StrictHostKeyChecking=no ${kubeMasterNodeServerUsername}@${kubeMasterNodeServerIp} 'docker rmi ${env.fullImageName}:${previousBuildId} || true'
                        """
                    }
                }
            }
        }

        stage('Image Delete on Jenkins VM') {
            steps {
                echo 'Removing Previous Docker Image on Jenkins VM'
                script {
                    def previousBuildId = "${env.BUILD_ID.toInteger() - 1}"
                    sh """
                    docker rmi ${env.fullImageName}:${previousBuildId} || true
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo 'Deploying to Kubernetes'
                sshagent (credentials: ['kube-master-ssh']) {
                    sh """
                    scp -o StrictHostKeyChecking=no ${manifest} ${kubeMasterNodeServerUsername}@${kubeMasterNodeServerIp}:~/app
                    ssh -o StrictHostKeyChecking=no ${kubeMasterNodeServerUsername}@${kubeMasterNodeServerIp} 'kubectl apply -f ~/app/${manifest}'
                    """
                }
            }
        }
    }

    post {
        success {
            slackSend (channel: '#jenkins', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        failure {
            slackSend (channel: '#jenkins', color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
    }
}
