pipeline {
    agent any
    environment {
        GITHUB_TOKEN = credentials('github_pat11AZGBPOI07U2aXe9fTLLu_UaIB6FBYdLCjefGSRU0v4mx2udwdmuau8VLfNzSoOVY6AP6EZNEI8XQxr8o')
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/MarcoM1992/task-manager-api'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
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
