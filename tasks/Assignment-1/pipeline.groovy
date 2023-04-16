pipeline {
    agent any
    tools { 
      maven 'MAVEN_HOME' 
      jdk 'JAVA_HOME' 
    }
    stages {
        stage('Build java binary') {
            steps {
				dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script{
						sh 'mvn clean package' 
					}
				}	
			}
        }

        stage('Build and push docker image') {
            steps {
			    dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script {

						sh "docker build -t suspicious-events-detector:latest -f Dockerfile ." 
						sh "docker push suspicious-events-detector:latest" 
					}
				}
            }
        }

        stage('Deploy to staging') {
            steps {
				dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script {
						sh "kubectl apply -f deployment-staging.yaml"
					}
				}	
            }
        }

        stage('Test deployment in staging') {
            steps {
				dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script {
				def app_url = sh(script: "kubectl get svc -l app=suspicious-events-detector -o jsonpath='{.items[0].spec.clusterIP}:{.items[0].spec.ports[0].port}'", returnStdout: true).trim()
				sh "curl http://${app_url}/"
					}
				}	
            }
        }
		
		stage('Deploy to production') {
            steps {
				dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script {
						sh "kubectl apply -f deployment-production.yaml"
					}
				}	
            }
        }
		
		stage('Test deployment in production') {
            steps {
				dir('C:/Users/muabdulk/Desktop/tasks/Assignment-1') {
					script {
					def app_url = sh(script: "kubectl get svc -l app=suspicious-events-detector -o jsonpath='{.items[0].spec.clusterIP}:{.items[0].spec.ports[0].port}'", returnStdout: true).trim()
				sh "curl http://${app_url}/"
					}
				}	
            }
        }
    }
}
