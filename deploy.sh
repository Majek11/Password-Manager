#!/bin/bash

# Build the application
./mvnw clean package -DskipTests

# Git commands
git add .
git commit -m "Deploy update $(date)"
git push railway main
chmod +x deploy.sh
source ~/.bashrc
export PATH=~/.npm-global/bin:$PATH

# Login to Railway
railway login

# Initialize your project
cd /home/majek/IdeaProjects/Password-Manager
railway init