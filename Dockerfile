### Get Python
FROM python:3.7

EXPOSE 8080

### 2. Get Java via the package manager
RUN apt-get update \
&& apt-get upgrade -y \
&& apt-get install openjdk-11-jre-headless -y\
&& python -m pip install imageio \
	&& pip install rawpy
RUN mkdir /home/server

WORKDIR /home/server/

COPY createThumbnail.py /home/server
COPY target/InstantGrade-Server-1.0-SNAPSHOT.jar /home/server

#CMD ["tail", "-f", "/dev/null"]
CMD ["java", "-jar", "InstantGrade-Server-1.0-SNAPSHOT.jar"]
