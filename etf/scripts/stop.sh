#!/bin/bash

# 8080 포트에서 실행 중인 프로세스 ID 찾기
CURRENT_PID=$(lsof -i :8080 -t)

if [ -z "$CURRENT_PID" ]; then
    echo "실행 중인 애플리케이션 없음."
else
    echo "애플리케이션 종료: $CURRENT_PID"
    kill -15 $CURRENT_PID # 정상 종료 시도
    sleep 5 # 종료될 때까지 대기
    # 만약 5초 후에도 프로세스가 남아있다면 강제 종료
    if ps -p $CURRENT_PID > /dev/null; then
        echo "5초 후에도 종료되지 않아 강제 종료($CURRENT_PID) 실행."
        kill -9 $CURRENT_PID
    fi
fi