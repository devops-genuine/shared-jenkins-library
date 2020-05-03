@Library('github.com/devops-genuine/shared-jenkins-library@master') _

pipeline {
    agent { label "master" }
    tools {
        maven 'Maven 3.6.3'
    }

    stages {
        stage("Echostage") {
            steps {
               echo "foo"
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean package' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
    }
    post {
        always {
            script {
                sendEmail(currentBuild, ['testemail@domain.tld'])
            }
        }
    }
}