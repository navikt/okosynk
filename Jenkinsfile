@Library('pipeline-lib') _
pipeline {
    agent any


    parameters {
        string(name: 'fasitEnvPreprod', defaultValue: 'q1', description: 'Fasit environment used for reading and exposing resources (preprod)')
        string(name: 'namespacePreprod', defaultValue: 'default', description: 'Nais namespace (preprod)')
        booleanParam(name: 'gatling', defaultValue: false, description: 'Whether to run Gatling tests as part of the build')
        booleanParam(name: 'deployProd', defaultValue: false, description: 'Whether to automatically deploy master branch to prod')
    }

    environment {
        APPLICATION_NAME = 'okosynk'
        APPLICATION_VERSION = version()
        APPLICATION_SERVICE = 'gosys'
        APPLICATION_COMPONENT = 'okosynk'
        FASIT_ENV = "${params.fasitEnvPreprod}"
        NAMESPACE = "${params.namespacePreprod}"
        RUN_GATLING = "${params.gatling}"
        DEPLOY_TO_PROD = "${params.deployProd}"
    }

    tools {
        maven "maven3"
        jdk "java8"
    }

    options {
        timestamps()
    }

    stages {

        stage('Maven Build and unit & integration tests)') {

            environment {
                CURRENT_STAGE = "${env.STAGE_NAME}"
            }

            steps {
                script {
                    sh "mvn clean install"
                }
            }
        }

        stage('Build and push docker image') {
            environment {
                CURRENT_STAGE = "${env.STAGE_NAME}"
            }
            steps {
                dockerUtils 'buildAndPush'
            }
        }

        stage('Run gatling-tests') {
            environment {
                CURRENT_STAGE = "${env.STAGE_NAME}"
            }
            when { environment name: 'RUN_GATLING', value: 'true' }
            steps {
                script {
                    sh "mvn gatling:test"
                }
            }
        }
    }

    post {
        always {
echo '1-oor'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
// echo '2-oor'
//            junit '**/target/surefire-reports/*.xml'
echo '3-oor'
            gatlingArchive()
echo '4-oor'

            script {
                if (currentBuild.result == 'ABORTED') {
                    slack status: 'aborted'
                }
            }
            dockerUtils 'prune'
            deleteDir()
        }
        success {
            slack status: 'success'
        }
        failure {
            slack status: 'failure'
        }
    }
}
