#!/bin/bash

#wget -qO- https://get.docker.com/ | sh

echo "Checking docker..."

installed=`which docker | grep docker`

if [ -n "$installed" ]; then
    service docker restart
    echo "Docker is running."
else
    echo "Docker is not installed, please install docker first."
    exit 1
    #echo "Installing Docker..."
    #docker1.9
    #if [ -e /sbin/chkconfig ] ; then
	  #   yum update -y 
    #fi    
    #wget -qO- https://get.docker.com/ | sh  
    
		#if [ -e /sbin/chkconfig ] ; then
	  #   service docker start
		#	 chkconfig docker on
    #fi
        
    #sleep 1
    #running=`service docker status | grep running`
    #if [ -n "$running" ]; then
    #    echo "Docker is running."
    #else
    #    echo "Install docker failed, please check network."
    #    exit 1
    #fi
fi

echo "anti ddos image is loading..."

docker stop antiddos > /dev/null 2>&1

sleep 1
docker rm antiddos > /dev/null 2>&1

sleep 1
docker rmi antiddos > /dev/null 2>&1

sleep 1
docker load --input /opt/antiddos/lib/antiddos.tar > /dev/null 2>&1

sleep 1
image=`docker images | grep antiddos`
if [ -n "$image" ]; then
    echo "Load anti ddos image successfully, anti ddos is starting..."
else
    echo "Load anti ddos image failed, please contact cetc 32"
    exit 1
fi 

docker run -t --rm --name="antiddos" -p 443:8443 -p 514:514/udp -p 3306:3306 -p 9200:9200 -p 9400:9400 -v /opt/antiddos/data/elasticsearch:/var/lib/elasticsearch/ -v /opt/antiddos/data/mysql/:/opt/anti-ddos/data/mysql -v /var/log/antiddos:/var/lib/tomcat7/webapps/ROOT/data/logs/ antiddos& > /dev/null 2>&1

sleep 10
container=`docker ps | grep antiddos`
if [ -n "$container" ]; then
    echo "anti ddos is runnging"
else
    echo "Start anti ddos failed, please contact cetc 32"
    exit 1
fi

exit 0





