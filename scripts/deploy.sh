#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/reg.rsa \
    dispatcher/target/dispatcher-1.0.jar \
    dronus@151.248.121.37:/home/dronus/

scp -i ~/.ssh/reg.rsa \
    node/target/node-1.0.jar \
    dronus@151.248.121.37:/home/dronus/

echo 'Restart server...'

ssh -i ~/.ssh/reg.rsa -ttttt dronus@151.248.121.37 << EOF
pgrep java | xargs kill -9
nohup java -jar dispatcher-1.0.jar > ./logs/dispatcher-log.txt 2>&1 &
nohup java -jar node-1.0.jar > ./logs/node-log.txt 2>&1 &
EOF

echo 'Bye'
