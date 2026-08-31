echo 'call start.sh'

java -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Tokyo -Dlogback.configurationFile=./logback.xml -jar ../target/apis-log-4.5.10-fat.jar -conf ./config.json

echo '... done'
