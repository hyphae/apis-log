echo 'call start.sh'

java -Djava.net.preferIPv4Stack=true -Duser.timezone=Asia/Tokyo -Djava.util.logging.config.file=./logging.properties -jar ../build/libs/apis-log-3.9.14-fat.jar -conf ./config.json

echo '... done'
