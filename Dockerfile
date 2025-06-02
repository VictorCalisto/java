FROM openjdk:25-jdk

WORKDIR /app

COPY . /app

RUN find . -name "*.java" > sources.txt && javac @sources.txt

CMD ["sleep", "infinity"]
