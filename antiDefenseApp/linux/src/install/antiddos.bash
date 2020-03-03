#!/bin/bash
#
# Copyright (c) <201x> <cetc 32.> and others.  All rights reserved.
#
# This program and the accompanying materials are made available under the terms of the Eclipse Public License
# v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
# @author cetc 32
# @version 0.1
#

#
# /etc/init.d/antiddos
#
# chkconfig: 2345 98 05
# description: Starts and stops antiddos
### BEGIN INIT INFO
# Provides: antiddos
# Required-Start: docker
# Required-Stop: docker
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
### END INIT INFO

NAME=antiddos
PIDFILE=/var/run/$NAME/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME
ANTIDDOS_HOME=/opt/$NAME/lib
OUTFILE=/var/log/$NAME/output.log
ANTIDDOS_FIFO=/var/tmp/rsyslog-df-pipe
ANTIDDOS_USER=root
ANTIDDOS_RUNnING=`docker ps -a | grep antiddos`

# Find antiddos installation
if [ ! -d $ANTIDDOS_HOME ]; then
	echo "antiddos doesn't seem to be installed"
	exit -1
fi	

function usage {
    echo "Usage: $0 {start|stop|restart|status [level]}"
    exit 1
}

#
# Function that returns 0 if process is running, or nonzero if not.
#
# The nonzero value is 3 if the process is simply not running, and 1 if the
# process is not running but the pidfile exists (to match the exit codes for
# the "status" command; see LSB core spec 3.1, section 20.2)
#
CMD_PATT="df.aggregate"
is_running()
{
  ANTIDDOS_RUNNING=`docker ps | grep antiddos`

  if [ -n "$ANTIDDOS_RUNNING" ]; then
    return 0        
  fi
  return 1
}

#
# Function that starts the daemon/service
#
do_start()
{
  
  sleep 1
  

  ANTIDDOS_RUNNING=`docker ps | grep antiddos`
  if [ -n "$ANTIDDOS_RUNNING" ]; then
    return 1        
  fi
 
  docker rm antiddos
  docker run -t --rm --name="antiddos" -p 443:8443 -p 514:514/udp -p 3306:3306 -p 9200:9200 -p 9400:9400 -v /opt/antiddos/data/elasticsearch:/var/lib/elasticsearch/ -v /opt/antiddos/data/mysql/:/opt/anti-ddos/data/mysql -v /var/log/antiddos:/var/lib/tomcat7/webapps/ROOT/data/logs/ antiddos& > /dev/null 2>&1

  sleep 5

  ANTIDDOS_RUNNING=`docker ps | grep antiddos`

  if [ -n "$ANTIDDOS_RUNNING" ]; then
    return 0       
  fi

  return 2    	  
}

#
# Function that stops the daemon/service
#
do_stop()
{
	# Return
	#   0 if daemon has been stopped
	#   1 if daemon was already stopped
	#   2 if daemon could not be stopped
	#   other if a failure occurred

  ANTIDDOS_RUNNING=`docker ps | grep antiddos`
  if [ -n "$ANTIDDOS_RUNNING" ]; then
    docker exec antiddos java -cp /usr/share/anti-ddos/antiDefense-1.0-SNAPSHOT.jar com.cetc.security.ddos.defense.main.CloseEventMain
    sleep 30
    docker stop antiddos
    docker rm antiddos
    return 1 
  fi
  return 0

}

case "$1" in
  start)
	do_start  > /dev/null 2>&1
	case "$?" in
		0) echo "$NAME is running" ;;  
		1) echo "$NAME is already running!";;
		2) echo "$NAME is not running" ;;
	esac
	;;
  stop)
	do_stop  > /dev/null 2>&1
	stat=$?
	case "$stat" in
		0) echo "$NAME has been stopped" ;;
		1) echo "$NAME is not running" ;;
	esac
	exit "$stat"	
	;;
  restart)
	do_stop  > /dev/null 2>&1
	case "$?" in
		0) echo "$NAME has been stopped" ;;
		1) echo "$NAME is not running" ;;
	esac
	do_start  > /dev/null 2>&1
	stat=$?
	case "$stat" in
		0) echo "$NAME is running" ;;
		1) echo "$NAME is already running" ;;
		2) echo "$NAME is not running" ;;
	esac
	exit "$stat"
	;;
  status)
    is_running
    stat=$?
    case "$stat" in
    	0) echo "$NAME is running" ;;
    	1) echo "$NAME is not running" ;;
    esac
    exit "$stat"
    ;;
  *)
	usage >&2
	;;
esac
