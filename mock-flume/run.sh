rm -R `pwd`/captures
mkdir -p `pwd`/captures
docker run -it --rm -p 44444:44444 -h localhost --name mock-flume --volume `pwd`/captures:/opt/apache-flume/captures maddogtechnology-docker-develop.jfrog.io/mock-flume:latest