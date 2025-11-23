pipeline {
    agent any
    tools{
        maven 'maven'
    }
    stages{
        stage('Build maven'){
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/EliasZunigaUSACH/Tingeso2025_1']])
					dir("payroll-backend"){
                		bat 'mvn clean package'
					}
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build docker image'){
            steps{
                script{
                    bat 'docker build -t eliaszngusach/payroll-backend:latest .'
                }
            }
        }
        stage('Push image to Docker Hub'){
            steps{
                script{
                   withCredentials([string(credentialsId: 'docker-credentials')])
                   bat 'docker push eliaszngusach/payroll-backend:latest'
                }
            }
        }
    }
}
