FROM openjdk:12

WORKDIR /root/storage
COPY / /root/storage

#Install JDK
#RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*

#ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
#ENV PATH $PATH:$JAVA_HOME/bin

#Compile
RUN javac src/StoragePassword.java

ENTRYPOINT ["java","StoragePassword.java"]