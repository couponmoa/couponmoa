#!/bin/bash

# ✅ 환경 변수 수동 로드
source /home/ubuntu/.profile

echo ">>> 기존 컨테이너 중지 및 삭제" >> /home/ubuntu/deploy.log
docker stop couponmoa || true
docker rm couponmoa || true

echo ">>> Docker 이미지 빌드" >> /home/ubuntu/deploy.log
docker build -t couponmoa-prod /home/ubuntu/todo >> /home/ubuntu/deploy.log 2>> /home/ubuntu/deploy_err.log

echo ">>> Docker 컨테이너 실행" >> /home/ubuntu/deploy.log
docker run -d \
  --name couponmoa \
  -e MYSQL_URL=$MYSQL_URL \
  -e DB_USERNAME=$DB_USERNAME \
  -e DB_PASSWORD=$DB_PASSWORD \
  -e REDIS_HOST=$REDIS_HOST \
  -e REDIS_PORT=$REDIS_PORT \
  -e JWT_SECRET_KEY=$JWT_SECRET_KEY \
  -p 8080:8080 \
  couponmoa-prod >> /home/ubuntu/deploy.log 2>> /home/ubuntu/deploy_err.log

echo "✅ Docker 기반 배포 완료" >> /home/ubuntu/deploy