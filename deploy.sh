#!/bin/bash

# Build & Package
echo "Building the project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "Build failed, exiting."
  exit 1
fi

# Docker Image Compose
echo "Building Docker image..."
docker build -t transaction-web .
docker tag transaction-web:latest 864981757274.dkr.ecr.ap-east-1.amazonaws.com/test/transaction-web:latest

if [ $? -ne 0 ]; then
  echo "Docker build failed, exiting."
  exit 1
fi

# Push Docker Image to AWS ECR 
echo "Logging into AWS ECR..."
aws ecr get-login-password --region ap-east-1 | docker login --username AWS --password-stdin 864981757274.dkr.ecr.ap-east-1.amazonaws.com

if [ $? -ne 0 ]; then
  echo "AWS ECR login failed, exiting."
  exit 1
fi

echo "Pushing Docker image to AWS ECR..."
docker push 864981757274.dkr.ecr.ap-east-1.amazonaws.com/test/transaction-web:latest

if [ $? -ne 0 ]; then
  echo "Docker push failed, exiting."
  exit 1
fi

# 5. 创建K8s Secret用于ECR认证
echo "Creating Kubernetes secret for AWS ECR..."
kubectl create secret docker-registry regcred \
  --docker-server=aws_account_id.dkr.ecr.$AWS_REGION.amazonaws.com \
  --docker-username=AWS \
  --docker-password=$(aws ecr get-login-password --region $AWS_REGION) \
  --docker-email=your-email@example.com --namespace $K8S_NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

if [ $? -ne 0 ]; then
  echo "Kubernetes secret creation failed, exiting."
  exit 1
fi

# Deloy AWS deployment & service & auto-scaling
echo "Deploy AWS ECS Cluster..."
kubectl apply -f ./deployments/

if [ $? -ne 0 ]; then
  echo "K8s apply failed, exiting."
  exit 1
fi

echo "Deployment completed successfully!"
