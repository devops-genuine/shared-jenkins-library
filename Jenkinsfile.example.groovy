pipeline {
    agent { label "master" }

    libraries {
        lib('github.com/devops-genuine/shared-jenkins-library')
    }

    stages {
        stage("Echostage") {
            steps {
               echo "foo"
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