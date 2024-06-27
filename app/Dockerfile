FROM gradle:8.7.0-jdk-21-and-22

WORKDIR /app

COPY . .

RUN gradle installShadowDist

CMD ./build/install/app-shadow/bin/app