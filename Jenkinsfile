#!/usr/bin/env groovy

@Library('sd')_
def kubeLabel = getKubeLabel()

pipeline {

  agent {
    kubernetes {
      label "${kubeLabel}"
      cloud 'Kube mwdevel'
      defaultContainer 'runner'
      inheritFrom 'ci-template'
    }
  }
 
  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }
  
  stages {
    stage('build') {
      steps {
        sh 'mvn -B clean compile'
      }
    }

    stage('test') {
      steps {
        sh 'mvn -B clean package'
      }

      post {
        always {
          junit '**/target/surefire-reports/TEST-*.xml'
        }
      }
    }
    
  }
  
  post {
    failure {
      slackSend color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)"
    }
    
    changed {
      script{
        if('SUCCESS'.equals(currentBuild.result)) {
          slackSend color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Back to normal (<${env.BUILD_URL}|Open>)"
        }
      }
    }
  }
}
