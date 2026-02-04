pipeline {
    agent any
    
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'production'], description: 'Select deployment environment')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests?')
        string(name: 'BRANCH_NAME', defaultValue: 'nikithabranch1', description: 'Branch to build')
        text(name: 'DEPLOY_NOTES', defaultValue: '', description: 'Deployment notes (optional)')
    }
    
    tools {
        maven 'Maven'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                echo "Environment: ${params.ENVIRONMENT}"
                echo "Skip Tests: ${params.SKIP_TESTS}"
                echo "Branch: ${params.BRANCH_NAME}"
                echo "Deploy Notes: ${params.DEPLOY_NOTES}"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/nikimanvi/Maven_jenkins_project.git',
                        credentialsId: 'GITHUB_PATLatest'
                    ]]
                ])
                bat 'java -version || echo "Java not found in PATH"'
                bat 'echo PATH: %PATH%'
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean compile'
            }
        }
        
        stage('Test') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                echo 'Running tests...'
                bat 'mvn test'
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
                bat 'mvn package -DskipTests'
            }
        }
        
        stage('Archive') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying to ${params.ENVIRONMENT} environment..."
                script {
                    if (params.ENVIRONMENT == 'production') {
                        echo '⚠️ PRODUCTION DEPLOYMENT!'
                        echo 'Deploy notes: ' + params.DEPLOY_NOTES
                    } else {
                        echo "Deploying to ${params.ENVIRONMENT}..."
                    }
                }
                echo 'Deployment completed!'
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
                to: 'nikisuresh06@gmail.com'
            )
        }
        failure {
            echo 'Build failed!'
            emailext (
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                to: 'nikisuresh06@gmail.com'
            )
        }
        unstable {
            echo 'Build is unstable!'
            emailext (
                subject: "UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                to: 'nikisuresh06@gmail.com'
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
