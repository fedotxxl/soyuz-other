#docker build -f mmrJava8.dockerfile -t fbelov/mmr-8 .
#docker push fbelov/mmr-8

#base image
FROM fbelov/java-8

#copy docker file
COPY mmrJava8.dockerfile /Dockerfile

#copy mmr.py
COPY mmr.py /mmr.py

#install python 2.7
RUN add-apt-repository ppa:fkrull/deadsnakes -y
RUN apt-get update
RUN apt-get install -y python2.7
RUN ln -s /usr/bin/python2.7 /usr/bin/python