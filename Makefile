
build:
	./gradlew build shadowJar

clean:
	./gradlew clean
	rm -rf *.log *.err build

install:
	./gradlew publishToMavenLocal

doc:
	./gradlew javadoc
	