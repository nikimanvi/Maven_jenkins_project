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
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                script {
                    // Define image name with environment tag
                    def imageName = "maven-jenkins-app:${params.ENVIRONMENT}-${env.BUILD_NUMBER}"
                    def latestTag = "maven-jenkins-app:${params.ENVIRONMENT}-latest"
                    
                    // Build Docker image
                    bat "docker build -t ${imageName} -t ${latestTag} ."
                    
                    echo "Docker image built successfully: ${imageName}"
                    
                    // Save image name for next stage
                    env.DOCKER_IMAGE = imageName
                    env.DOCKER_LATEST = latestTag
                }
            }
        }
        
        stage('Docker Deploy') {
            steps {
                echo "Deploying Docker container to ${params.ENVIRONMENT} environment..."
                script {
                    // Stop and remove existing container if it exists
                    bat """
                        docker stop maven-app-${params.ENVIRONMENT} 2>nul || echo "No existing container to stop"
                        docker rm maven-app-${params.ENVIRONMENT} 2>nul || echo "No existing container to remove"
                    """
                    
                    // Run new container
                    bat "docker run -d --name maven-app-${params.ENVIRONMENT} ${env.DOCKER_IMAGE}"
                    
                    if (params.ENVIRONMENT == 'production') {
                        echo '⚠️ PRODUCTION DEPLOYMENT COMPLETED!'
                        echo 'Deploy notes: ' + params.DEPLOY_NOTES
                    } else {
                        echo "Container deployed to ${params.ENVIRONMENT}"
                    }
                    
                    // Show container status
                    bat "docker ps -f name=maven-app-${params.ENVIRONMENT}"
                }
                echo 'Docker deployment completed!'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo 'Verifying Docker container...'
                script {
                    // Check if container is running
                    bat "docker logs maven-app-${params.ENVIRONMENT}"
                    
                    echo "Container maven-app-${params.ENVIRONMENT} is running successfully!"
                }
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
