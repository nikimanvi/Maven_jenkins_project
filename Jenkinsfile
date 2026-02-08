pipeline {
    agent any
    
    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'production'], description: 'Select deployment environment')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests?')
        booleanParam(name: 'SKIP_DEPLOYMENT', defaultValue: true, description: 'Skip Docker deployment? (Only build image)')
        booleanParam(name: 'PUSH_TO_DOCKERHUB', defaultValue: false, description: 'Push image to Docker Hub?')
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
                    // Docker Hub username
                    def dockerHubUser = "nikithamanvi"
                    
                    // Define image names with Docker Hub username for registry push
                    def imageName = "${dockerHubUser}/maven-jenkins-app:${params.ENVIRONMENT}-${env.BUILD_NUMBER}"
                    def latestTag = "${dockerHubUser}/maven-jenkins-app:${params.ENVIRONMENT}-latest"
                    
                    // Also create local tags without username for local use
                    def localImage = "maven-jenkins-app:${params.ENVIRONMENT}-${env.BUILD_NUMBER}"
                    def localLatest = "maven-jenkins-app:${params.ENVIRONMENT}-latest"
                    
                    // Build Docker image with all tags
                    bat "docker build -t ${imageName} -t ${latestTag} -t ${localImage} -t ${localLatest} ."
                    
                    echo "Docker image built successfully: ${imageName}"
                    
                    // Save image names for next stages
                    env.DOCKER_IMAGE = imageName
                    env.DOCKER_LATEST = latestTag
                    env.LOCAL_IMAGE = localImage
                }
            }
        }
        
        stage('Push to Docker Hub') {
            when {
                expression { params.PUSH_TO_DOCKERHUB == true }
            }
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    // Login to Docker Hub using Jenkins credentials
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        // Login to Docker Hub
                        bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                        
                        // Push both tags
                        bat "docker push ${env.DOCKER_IMAGE}"
                        bat "docker push ${env.DOCKER_LATEST}"
                        
                        echo "✅ Images pushed successfully to Docker Hub!"
                        echo "- ${env.DOCKER_IMAGE}"
                        echo "- ${env.DOCKER_LATEST}"
                        echo ""
                        echo "Anyone can now pull your image with:"
                        echo "docker pull ${env.DOCKER_IMAGE}"
                        
                        // Logout for security
                        bat "docker logout"
                    }
                }
            }
        }
        
        stage('Docker Deploy') {
            when {
                expression { params.SKIP_DEPLOYMENT == false }
            }
            steps {
                echo "Deploying Docker container to ${params.ENVIRONMENT} environment..."
                script {
                    // Stop and remove existing container if it exists
                    bat """
                        docker stop maven-app-${params.ENVIRONMENT} 2>nul || echo "No existing container to stop"
                        docker rm maven-app-${params.ENVIRONMENT} 2>nul || echo "No existing container to remove"
                    """
                    
                    // Run new container (non-detached for apps that exit immediately)
                    // Use local image tag for deployment
                    bat "docker run --name maven-app-${params.ENVIRONMENT} ${env.LOCAL_IMAGE}"
                    
                    if (params.ENVIRONMENT == 'production') {
                        echo '⚠️ PRODUCTION DEPLOYMENT COMPLETED!'
                        echo 'Deploy notes: ' + params.DEPLOY_NOTES
                    } else {
                        echo "Container deployed to ${params.ENVIRONMENT}"
                    }
                }
                echo 'Docker deployment completed!'
            }
        }
        
        stage('Verify Deployment') {
            when {
                expression { params.SKIP_DEPLOYMENT == false }
            }
            steps {
                echo 'Verifying Docker container...'
                script {
                    // Check if container ran successfully
                    bat "docker logs maven-app-${params.ENVIRONMENT}"
                    
                    echo "Container maven-app-${params.ENVIRONMENT} executed successfully!"
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
