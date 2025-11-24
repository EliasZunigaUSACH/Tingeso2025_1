pipeline {
    agent any
    tools{
        maven 'maven'
    }
    stages{
        stage('Build maven'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/EliasZunigaUSACH/Tingeso2025_1']])
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

        stage('Build and Push Docker Image'){
            steps{
				dir("payroll-backend"){
	                script{
						withDockerRegistry(credentialsId: 'docker-credentials'){
	                    	bat 'docker build -t eliaszngusach/payroll-backend:latest .'
							bat 'docker push eliaszngusach/payroll-backend:latest'
						}
	                }
				}
            }
        }
    }
}
