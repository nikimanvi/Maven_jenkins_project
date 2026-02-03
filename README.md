# Maven Jenkins Project

A simple Maven project configured for Jenkins CI/CD pipeline execution.

## Project Structure

```
maven-jenkins-project/
├── pom.xml
├── Jenkinsfile
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── App.java
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── AppTest.java
└── README.md
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Jenkins (for CI/CD pipeline)

## Local Build

To build the project locally:

```bash
mvn clean install
```

To run tests:

```bash
mvn test
```

To package the application:

```bash
mvn package
```

To run the application:

```bash
java -jar target/maven-jenkins-project-1.0-SNAPSHOT.jar
```

## Jenkins Setup

1. **Configure Jenkins Tools:**
   - Go to Jenkins → Manage Jenkins → Global Tool Configuration
   - Add Maven installation (name it "Maven 3.9.0")
   - Add JDK installation (name it "JDK 11")

2. **Create Jenkins Pipeline:**
   - Create a new Pipeline job in Jenkins
   - In the Pipeline section, select "Pipeline script from SCM"
   - Configure your SCM (Git, etc.)
   - Jenkins will automatically use the Jenkinsfile in the repository

3. **Alternative - Direct Pipeline:**
   - Create a new Pipeline job
   - Copy the contents of Jenkinsfile into the Pipeline script section

## Pipeline Stages

The Jenkins pipeline includes the following stages:

1. **Checkout** - Checks out the source code
2. **Build** - Compiles the Java code
3. **Test** - Runs unit tests and publishes results
4. **Package** - Creates a JAR file
5. **Archive** - Archives the build artifacts

## Features

- Standard Maven project structure
- JUnit 4 for unit testing
- Jenkins declarative pipeline
- Automated testing and artifact archiving
- Clean workspace after build

## License

This is a sample project for demonstration purposes.
