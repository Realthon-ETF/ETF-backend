#!/bin/bash

REPOSITORY=/home/ubuntu/app
JAR_NAME=etf-0.0.1-SNAPSHOT.jar # 프로젝트에 맞는 JAR 파일 이름으로 변경

echo ">>> 애플리케이션 빌드 파일 복사 및 실행 권한 부여"
# CodeDeploy는 파일을 /home/ubuntu/app 에 복사했을 것입니다.
cd $REPOSITORY
chmod +x $JAR_NAME

echo ">>> Spring Boot 애플리케이션 시작"
# nohup을 사용하여 백그라운드에서 실행하고 로그를 남깁니다.
# JVM 옵션: OOM 방지 및 메모리 설정
nohup java -jar -Dspring.profiles.active=prod \
    $REPOSITORY/$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

echo ">>> 시작 완료. 로그 확인 중..."
# 10초 대기 후 로그 10줄 출력
sleep 10
tail -n 10 $REPOSITORY/nohup.out