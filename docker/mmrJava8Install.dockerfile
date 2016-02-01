#docker build -t fbelov/mmr-8-install:latest .
#docker push fbelov/mmr-8-install:latest

#base image
FROM fbelov/mmr-8

#install rpm on container start
CMD /mmr.py install $APP_ID && /sbin/my_init