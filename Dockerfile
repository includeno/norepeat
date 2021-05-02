FROM redis:6.0.8
RUN /bin/sh -c 'apt-get update  && apt-get upgrade -y  && apt-get install git -y && apt-get install vim -y'

#COPY redis.conf /usr/local/etc/redis/redis.conf
#CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]