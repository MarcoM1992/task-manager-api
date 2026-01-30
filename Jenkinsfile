pipeline {
    agent any
    environment {
        GITHUB_TOKEN = credentials('githubtoken') // solo per API o script
    }

    
    tools {
        maven 'Maven-3.9'
        jdk 'jdk-21.0.8'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/MarcoM1992/task-manager-api',
                    credentialsId: 'githubtoken' // <- checkout sicuro
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
                sh 'mvn clean package -DskipTests'

            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
    }
}
