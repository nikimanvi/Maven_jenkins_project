pipeline {
    agent any
    
    tools {
        maven 'Maven'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/nikithabranch1']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/nikimanvi/Maven_jenkins_project.git',
                        credentialsId: 'GITHUB_PATLatest'  // Use the credential ID from Jenkins
                    ]]
                ])
                // Check available Java
                bat 'java -version || echo "Java not found in PATH"'
                bat 'echo PATH: %PATH%'
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the project...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Archive') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        success {
            echo 'Build completed successfully!'
            emailext (
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                to: 'your-email@example.com'
            )
        }
        failure {
            echo 'Build failed!'
            emailext (
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
                to: 'your-email@example.com'
            )
        }
        unstable {
            echo 'Build is unstable!'
            emailext (
                subject: "UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: 'your-email@example.com'
            )
        }
        always {
            echo 'Sending notification...'
            // Alternative simple email notification
            // mail to: 'your-email@example.com',
            //      subject: "Build ${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
            //      body: "Build ${env.BUILD_NUMBER} finished with status: ${currentBuild.currentResult}\n\nCheck: ${env.BUILD_URL}"
            cleanWs()
        }
    }
}
