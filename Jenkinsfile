@Library('pipeline-lib') _
pipeline {
    agent any

    parameters {
        string(name: 'yamlFile', defaultValue: 'app-preprod.yaml', description: 'Yaml fil som applies')
    }

    environment {
        APPLICATION_NAME = 'okosynk'
        APPLICATION_VERSION = version()
    }

    tools {
        maven "maven-3.6.1"
        jdk "8"
    }

    stages {
        stage('Maven Build') {
            steps {
                script {
                    sh "mvn clean install"
                }
            }
        }

        stage('Build and push docker image') {
            steps {
                script {
                    echo "Done!"
                    echo ${env.APPLICATION_VERSION}
                }

            }
        }

    }
}