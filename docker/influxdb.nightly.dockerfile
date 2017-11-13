#docker build -f influxdb.nightly.dockerfile -t fbelov/influxdb-nightly .
#docker push fbelov/influxdb-nightly

#base image
FROM phusion/baseimage

#copy docker file
COPY influxdb.nightly.dockerfile /Dockerfile

#install nightly influxdb
RUN wget https://dl.influxdata.com/influxdb/nightlies/influxdb_nightly_amd64.deb
RUN sudo dpkg -i influxdb_nightly_amd64.deb

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]