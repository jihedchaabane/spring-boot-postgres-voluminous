pipeline {
    agent any
    environment {
        // Nom de l'image Docker
        DOCKER_IMAGE = 'jihed123/spring-boot-postgres-voluminous:0.0.1-SNAPSHOT'
        // Nom du conteneur
        CONTAINER_NAME = 'container-voluminous'
        // Port de l'application
        APP_PORT = '2502'
    }
    stages {
        stage('Checkout') {
            steps {
                // Récupérer le code depuis le dépôt Git
                checkout scm
            }
        }
        stage('Build Module') {
            steps {
                script {
                    // Construire le module Spring Boot
                    sh """
                        /opt/maven/bin/mvn clean package
                    """
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Construire l'image Docker à partir du Dockerfile dans le module
                    sh """
                        docker build -t ${DOCKER_IMAGE} .
                    """
                }
            }
        }
		

//        stage('Push Docker Image') {
//            steps {
//                script {
//                    // Pousser l'image vers un registre (optionnel)
//                  docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials-id') {
//                      sh """
//                         docker push ${DOCKER_IMAGE}
//                        """
//                    }
//                }
//            }
//        }
        stage('Stop and Remove Existing Container') {
            steps {
                script {
                    // Arréter et supprimer le conteneur s'il existe
                    sh """
                        docker stop ${CONTAINER_NAME} || true
                        docker rm ${CONTAINER_NAME} || true
                    """
                }
            }
        }
		
		stage('Run Docker Container') {
            steps {
                script {
                    // Lancer le conteneur
                    sh """
                        docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${APP_PORT} ${DOCKER_IMAGE}
                    """
                }
            }
        }

//        stage('Verify Container') {
//            steps {
//                script {
//                    // Vérifier que le conteneur est en cours d'exécution
//                    sh """
//                        sleep 10 # Attendre que le conteneur démarre
//                        docker ps --filter "name=${CONTAINER_NAME}" --filter "status=running" | grep ${CONTAINER_NAME}
//                    """
//                    // Vérifier que l'application répond
//                    sh """
//                        curl --fail http://localhost:${APP_PORT}/actuator/health || exit 1
//                    """
//                }
//            }
//        }

//		stage('Verify Container2') {
//			steps {
//				script {
//					sh """
//						# Attendre que le conteneur soit en cours d'exécution
//						sleep 10
//						docker ps --filter "name=${CONTAINER_NAME}" --filter "status=running" | grep ${CONTAINER_NAME}
//					"""
//					sh """
//						# Tenter la connexion plusieurs fois
//						for i in {1..10}; do
//							curl -v --fail http:localhost:${APP_PORT}/actuator/health && break
//							echo "Attente de l'application... (\$i/10)"
//							sleep 5
//						done
//						# Vérification finale
//						curl --fail http://localhost:${APP_PORT}/actuator/health || exit 1
//					"""
//				}
//			}
//		}

    }
    post {
        success {
            echo 'Application SpringBoot démarrée avec succés !'
        }
        failure {
            echo 'Echec du démarrage de l\'application.'
//            sh """
//                docker stop ${CONTAINER_NAME} || true
//                docker rm ${CONTAINER_NAME} || true
//            """
        }
    }
}