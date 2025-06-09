FROM openjdk:25-jdk

COPY ./app /app

WORKDIR /app

CMD ["/bin/bash"]
