echo 'call start.sh'

java -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Tokyo -Djava.util.logging.config.file=./logging.properties -jar ../target/apis-log-4.5.10-fat.jar -conf ./config.json

echo '... done'
