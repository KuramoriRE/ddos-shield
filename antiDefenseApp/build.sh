#!/bin/bash

ANTIVERSION=`cat build.conf | grep ANTI_DDOS_VERSION | sed 's/ANTI_DDOS_VERSION=//g' | tr -d ' '`
TIME_BUILD=`date "+%Y%m%d%H%M%S"`
WEBTYPE=`cat build.conf | grep WEB_TYPE | sed 's/WEB_TYPE=//g' | tr -d ' '`

#sh replace.sh
replace()
{
  find ../ -name log4j.properties   -print0 | xargs -0 perl -pi  -e "s/10.111.121.27/127.0.0.1/g"

  find ../ -name log1.js  -print0 | xargs -0 perl -pi  -e "s/\"10.111.121.27\"/\\$\llocation.host\(\)/g"

  find ../ -name log2.js  -print0 | xargs -0 perl -pi  -e "s/\"10.111.121.27\"/\\$\llocation.host\(\)/g"
  
  find ../ -name log3.js  -print0 | xargs -0 perl -pi  -e "s/\"10.111.121.27\"/\\$\llocation.host\(\)/g"

  find ../ -name FlowDataService.java  -print0 | xargs -0 perl -pi  -e "s/10.111.121.27/127.0.0.1/g"

  find ../ -name message.properties  -print0 | xargs -0 perl -pi  -e "s/10.111.121.15/127.0.0.1/g"

  find ../ -name log4j.properties   -print0 | xargs -0 perl -pi  -e "s/debug,appender1,appender2/debug,appender2/g"

  find ../ -name config.properties   -print0 | xargs -0 perl -pi  -e "s/10.111.121.27/127.0.0.1/g"

  find ../ -name config.properties   -print0 | xargs -0 perl -pi  -e "s/10.111.121.15/127.0.0.1/g"

  #find ../ -name log4j.properties   -print0 | xargs -0 perl -pi  -e "s/C\\\:\\\\\\\antiddos\\\\\\\/\/var\/log\/anti-ddos\//g"

  find ../ -name log4j.properties   -print0 | xargs -0 perl -pi  -e "s/C\\\:\\\\\\\antiddos\\\\\\\/\/var\/lib\/tomcat7\/webapps\/ROOT\/data\/logs\//g"
  
  find ../ -name log1.js  -print0 | xargs -0 perl -pi  -e "s/9200/9400/g"
  find ../ -name log1.js  -print0 | xargs -0 perl -pi  -e "s/http:/https:/g"

  find ../ -name log2.js  -print0 | xargs -0 perl -pi  -e "s/9200/9400/g"
  find ../ -name log2.js  -print0 | xargs -0 perl -pi  -e "s/http:/https:/g"
  
  find ../ -name log3.js  -print0 | xargs -0 perl -pi  -e "s/9200/9400/g"
  find ../ -name log3.js  -print0 | xargs -0 perl -pi  -e "s/http:/https:/g"

  find ./linux/ -name pom.xml  -print0 | xargs -0 perl -pi  -e "s/100.0/$ANTIVERSION/g" 
  
  if [ $WEBTYPE = http ]
  then
      find ./linux/ -name antiddos.bash  -print0 | xargs -0 perl -pi  -e "s/8443/8080/g" 
      find ./linux/ -name antiddos.bash  -print0 | xargs -0 perl -pi  -e "s/443/80/g" 
      find ./linux/ -name run.sh  -print0 | xargs -0 perl -pi  -e "s/8443/8080/g" 
      find ./linux/ -name run.sh  -print0 | xargs -0 perl -pi  -e "s/443/80/g" 
  fi

  return 
}

#sh mvn.sh
buildcode()
{
  mvn -f ../pom.xml clean install

  mkdir -p bin/ROOT/
  mkdir -p bin/anti-ddos/

  cp /root/.m2/repository/com/cetc/security/webAll/1.0-SNAPSHOT/webAll-1.0-SNAPSHOT.war ./bin/ROOT/
  cp /root/.m2/repository/com/cetc/security/antiDefense/1.0-SNAPSHOT/antiDefense-1.0-SNAPSHOT.jar ./bin/anti-ddos/

  cd bin/ROOT/
  jar -xf webAll-1.0-SNAPSHOT.war
  rm -rf webAll-1.0-SNAPSHOT.war

  cd ../
  tar cf anti-ddos.tar ./anti-ddos
  tar cf ROOT.tar ./ROOT

  rm -rf anti-ddos/
  rm -rf ROOT/
  
  cd ../

  pwd
  
  return 
}


createantistart()
{
  SHELLNAME=bin/start.sh

  echo "#!/bin/bash" > $SHELLNAME
  echo "" >> $SHELLNAME  
  echo "service rsyslog restart   > /dev/null 2>&1" >> $SHELLNAME
  echo "killall rsyslogd   > /dev/null 2>&1" >> $SHELLNAME
  echo "chown -R mysql:mysql /opt/anti-ddos/data/mysql" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/etc/init.d/elasticsearch start  > /dev/null 2>&1" >> $SHELLNAME 
  echo "/etc/init.d/redis-server start  > /dev/null 2>&1" >> $SHELLNAME
  echo "/etc/init.d/nginx start  > /dev/null 2>&1" >> $SHELLNAME
  echo "/etc/init.d/mysql start   > /dev/null 2>&1" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "sleep 1" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "if [ -f /opt/anti-ddos/data/mysql/bak/mysql.sql ]; then" >> $SHELLNAME
  echo '  CHANGE=`diff /opt/anti-ddos/data/mysql/bak/mysql.sql /opt/anti-ddos/data/mysql/mysql.sql`' >> $SHELLNAME
  echo '  if [ -n "$CHANGE" ]; then' >> $SHELLNAME
  echo "    mysql -u root -pcetc adconfig < /opt/anti-ddos/data/mysql/mysql.sql" >> $SHELLNAME
  echo "    cp /opt/anti-ddos/data/mysql/mysql.sql /opt/anti-ddos/data/mysql/bak/" >> $SHELLNAME  
  echo "  fi" >> $SHELLNAME
  echo "else" >> $SHELLNAME
  echo "  mysql -u root -pcetc adconfig < /opt/anti-ddos/data/mysql/mysql.sql" >> $SHELLNAME
  echo "  mkdir -p /opt/anti-ddos/data/mysql/bak/" >> $SHELLNAME
  echo "  cp /opt/anti-ddos/data/mysql/mysql.sql /opt/anti-ddos/data/mysql/bak/" >> $SHELLNAME
  echo "fi" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/opt/logstash/bin/antiddoslog.sh  > /dev/null 2>&1" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "sleep 10" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "chown -R tomcat7:tomcat7 /var/lib/tomcat7/webapps/ROOT" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "chmod 777 /var/lib/tomcat7/webapps/ROOT/data/logs/antiddossystemlog.txt" >> $SHELLNAME
  echo "chmod 777 /var/lib/tomcat7/webapps/ROOT/data/logs/antiddosoperationlog.txt" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "java -cp /usr/share/anti-ddos/antiDefense-1.0-SNAPSHOT.jar com.cetc.security.ddos.defense.main.DdosMain > /dev/null 2>&1 &" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/etc/init.d/tomcat7 start  > /dev/null 2>&1" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo 'echo *****start antiddos @`date`***** >> /var/lib/tomcat7/webapps/ROOT/data/logs/antiddossystemlog.txt' >> $SHELLNAME
  echo 'echo *****start antiddos @`date`***** >> /var/lib/tomcat7/webapps/ROOT/data/logs/antiddosoperationlog.txt' >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/bin/bash" >> $SHELLNAME
 
  return 

}


createrDockerfilerun()
{
  SHELLNAME=bin/Dockerfile.run

  echo "FROM antiddosbase" > $SHELLNAME 
  echo "MAINTAINER cetc" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "RUN rm -rf /usr/share/anti-ddos/" >> $SHELLNAME
  echo "RUN rm -rf /var/lib/tomcat7/webapps/ROOT/" >> $SHELLNAME 
  echo "RUN rm -rf /var/lib/tomcat7/server*" >> $SHELLNAME  
  echo 'RUN keytool -genkey -alias cetc -keyalg RSA -keypass cetcSDNAntiDDos -storepass cetcSDNAntiDDos -keystore /var/lib/tomcat7/server.keystore -validity 365 -dname "CN=antiddos, OU=cetc, C=cn"' >> $SHELLNAME
  echo "RUN keytool -export -trustcacerts -alias cetc -file /var/lib/tomcat7/server.cer -keystore /var/lib/tomcat7/server.keystore -storepass cetcSDNAntiDDos"  >> $SHELLNAME
  echo "RUN keytool -importkeystore -srcstoretype JKS -srckeystore /var/lib/tomcat7/server.keystore -srcstorepass cetcSDNAntiDDos -srcalias cetc -srckeypass cetcSDNAntiDDos -deststoretype PKCS12 -destkeystore /var/lib/tomcat7/server.p12 -deststorepass cetcSDNAntiDDos -destalias cetc -destkeypass cetcSDNAntiDDos -noprompt" >> $SHELLNAME
  echo "RUN openssl pkcs12 -in /var/lib/tomcat7/server.p12 -out /var/lib/tomcat7/server.pem.p12 -passin pass:cetcSDNAntiDDos  -passout pass:cetcSDNAntiDDos" >> $SHELLNAME  
  echo "RUN openssl rsa -in /var/lib/tomcat7/server.pem.p12 -passin pass:cetcSDNAntiDDos -out /var/lib/tomcat7/server.pem.key -passout pass:cetcSDNAntiDDos" >> $SHELLNAME
  echo "RUN openssl rsa -in /var/lib/tomcat7/server.pem.p12 -passin pass:cetcSDNAntiDDos -out /var/lib/tomcat7/server.pem.pub -pubout" >> $SHELLNAME
  echo "RUN openssl x509 -in /var/lib/tomcat7/server.pem.p12 -out /var/lib/tomcat7/server.pem.cer" >> $SHELLNAME
  if [ $WEBTYPE = http ]
  then
      echo "RUN sed -i 'N;100a-->' /etc/tomcat7/server.xml" >> $SHELLNAME
      echo "RUN sed -i 'N;94a<!--' /etc/tomcat7/server.xml" >> $SHELLNAME
      echo "RUN sed -i '84d' /etc/tomcat7/server.xml" >> $SHELLNAME
      echo "RUN sed -i '79d' /etc/tomcat7/server.xml" >> $SHELLNAME
  fi
  
  echo "" >> $SHELLNAME
  echo "ADD ./bin/anti-ddos.tar /usr/share/" >> $SHELLNAME
  echo "ADD ./bin/ROOT.tar /var/lib/tomcat7/webapps/" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "RUN mkdir -p /var/lib/tomcat7/webapps/ROOT/data/logs" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "ADD ./bin/start.sh /opt/anti-ddos/bin/start.sh" >> $SHELLNAME
  echo "ADD ./bin/version /opt/anti-ddos/" >> $SHELLNAME
  echo "CMD /bin/sh /opt/anti-ddos/bin/start.sh" >> $SHELLNAME

  return
}

createrDockerfileboot2iso()
{
  local SHELLNAME=bin/Dockerfile.boot2iso

  echo "FROM boot2docker/boot2docker"  > $SHELLNAME
  echo "MAINTAINER cetc" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo 'COPY ./bin/antiddos.tar.xz $ROOTFS/data/' >> $SHELLNAME
  echo 'COPY ./bin/antiddosdata.tar.xz $ROOTFS/data/' >> $SHELLNAME
  echo 'COPY ./bin/antiddos $ROOTFS/etc/rc.d/' >> $SHELLNAME
  echo -n 'RUN find $ROOTFS/etc/rc.d/ $ROOTFS/usr/local/etc/init.d/ ' >> $SHELLNAME
  echo "-exec chmod +x '{}' ';'" >> $SHELLNAME
  echo 'RUN echo "/etc/rc.d/antiddos" >> $ROOTFS/opt/bootscript.sh' >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo 'RUN echo "" >> $ROOTFS/etc/motd' >> $SHELLNAME
  echo 'RUN echo "  this is antiddos of cetc." >> $ROOTFS/etc/motd' >> $SHELLNAME
  echo 'RUN echo "" >> $ROOTFS/etc/motd' >> $SHELLNAME
  echo "" >> $SHELLNAME  
  echo 'RUN /make_iso.sh' >> $SHELLNAME
  echo 'CMD ["cat", "boot2docker.iso"]' >> $SHELLNAME

  return
}

createDockerfilemakeexe()
{
  local SHELLNAME=bin/Dockerfile.makeexe
  
  echo "FROM antiddosexebase" > $SHELLNAME
  echo "" >> $SHELLNAME
  echo "ENV DEBIAN_FRONTEND noninteractive" >> $SHELLNAME
  echo "ENV INSTALLER_VERSION $ANTIVERSION" >> $SHELLNAME
  echo "ENV MIXPANEL_TOKEN c306ae65c33d7d09fe3e546f36493a6e" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "COPY bin/boot2docker.iso  /bundle/" >> $SHELLNAME
  echo "COPY windows /installer" >> $SHELLNAME
  echo "WORKDIR /installer" >> $SHELLNAME
  echo "RUN rm -rf /tmp/.wine-0/" >> $SHELLNAME
  echo 'RUN wine ../innosetup/ISCC.exe Toolbox.iss /DMyAppVersion=$INSTALLER_VERSION /DMixpanelToken=$MIXPANEL_TOKEN' >> $SHELLNAME

  return 
}


createbooterstart()
{
  local SHELLNAME=bin/antiddos

  echo "#!/bin/sh" > $SHELLNAME
  echo "" >> $SHELLNAME
  echo "chmod 777 /data/antiddos.tar.xz" >> $SHELLNAME
  echo "chmod 777 /data/antiddosdata.tar.xz" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/usr/local/bin/unxz /data/antiddos.tar.xz" >> $SHELLNAME
  echo "/usr/local/bin/unxz /data/antiddosdata.tar.xz" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/usr/local/bin/docker load --input /data/antiddos.tar" >> $SHELLNAME
  echo "/usr/local/bin/docker load --input /data/antiddosdata.tar" >> $SHELLNAME
  echo "" >> $SHELLNAME
  echo "/usr/local/bin/docker run --name=antiddosdata antiddosdata" >> $SHELLNAME
  echo '/usr/local/bin/docker run -t --rm --name="antiddos" -p 80:8080 -p 9200:9200 -p 9400:9400 --volumes-from=antiddosdata  antiddos& > /dev/null 2>&1' >> $SHELLNAME
  echo "" >> $SHELLNAME

  return 
}


createversionfile()
{
  SHELLNAME=bin/version
  
  echo "antiddos version:$ANTIVERSION" > $SHELLNAME
  echo "build date:$TIME_BUILD" >> $SHELLNAME

  return 
}

getmysqlconf()
{
  rm -rf bin/mysql*

  docker rm antiddosbase 2>/dev/null || true
  docker run --name antiddosbase antiddosbase
  docker cp antiddosbase:/home/mysql.tar.xz ./bin/
  docker rm antiddosbase 2>/dev/null || true
 
  unxz ./bin/mysql.tar.xz  
  tar xf bin/mysql.tar -C bin/
  cp ./mysql.sql ./bin/mysql/
  mkdir -p ./bin/mysql/bak
  rm -rf ./bin/mysql.tar
 
  return 
}

#sh docker.sh
createrpmfie()
{
  docker stop antiddos  > /dev/null 2>&1
  docker rm antiddos  > /dev/null 2>&1
  docker rmi antiddos  > /dev/null 2>&1
 
  createversionfile;
  createantistart;
  createrDockerfilerun;

  docker build -t "antiddos" -f bin/Dockerfile.run  ./

  echo "Saving antiddos images..."
  docker save -o ./bin/antiddos.tar antiddos
  echo "Saving antiddos images...done"
  
  rm -rf ./bin/Dockerfile.run
  rm -rf ./bin/start.sh
  rm -rf ./bin/ROOT.tar
  rm -rf ./bin/anti-ddos.tar

  return
}

createrpmdir()
{
  mkdir -p ../rpm/
  return
}


#sh rpmbuild.sh
buildrpm()
{
  cp -r ./bin/mysql/ ./linux/
  cp -r ./bin/antiddos.tar ./linux/
  cp -r ./bin/version ./linux/
  
  mvn -f linux/pom.xml clean install

  mv ./linux/target/antiddos_$ANTIVERSION.deb ../rpm/antiddos_$ANTIVERSION.$TIME_BUILD.deb > /dev/null 2>&1 || true
  mv ./linux/target/rpm/anti*/RPMS/noarch/antiddos-$ANTIVERSION-1.noarch.rpm ../rpm/antiddos_$ANTIVERSION.$TIME_BUILD.rpm > /dev/null 2>&1 || true

  rm -rf ./linux/mysql/
  rm -rf ./linux/antiddos.tar
  rm -rf ./linux/version
  
  rm -rf ./linux/target/*

  return 
}

buildrpmonly()
{
  cp -r linux/pom.xml linux/pom.xml.bak

  cd ./linux/
  local startLine=`sed -n '/builddebstart/=' pom.xml`
  local endLine=`sed -n '/builddebend/=' pom.xml`
  sed -i $startLine','$endLine'd' pom.xml
  cd ../

  buildrpm

  rm -rf linux/pom.xml
  mv linux/pom.xml.bak linux/pom.xml

  return
} 

builddebonly()
{
  cp -r linux/pom.xml linux/pom.xml.bak

  local startLine=`sed -n '/<!--buildrpmstart-->/=' linux/pom.xml`
  local endLine=`sed -n '/<!--buildrpmend-->/=' linux/pom.xml`
  sed -i $startLine','$endLine'd' linux/pom.xml

  buildrpm

  rm -rf linux/pom.xml
  mv linux/pom.xml.bak linux/pom.xml

  return 
}


buildbundle()
{
  echo "Build anti ddos bundle..."
  cp -r ./linux/src/deb/control/postinst ./
  cp -r ./linux/src/deb/control/prerm ./
  cp -r ./linux/src/bundle/antiddos.bundle ./tmp.bundle  
  cp -r prerm uninstall.sh
  
  perl -pi  -e "s/#\!\/bin\/sh//g" prerm
  perl -pi  -e "s/#\!\/bin\/sh//g" postinst

  sed '/##postinstend##/{h;s/.*/cat postinst/e;G}' tmp.bundle > tmp1.bundle
  sed '/##prermend##/{h;s/.*/cat prerm/e;G}' tmp1.bundle > antiddos.bundle
  rm -rf postinst tmp.bundle tmp1.bundle prerm

  local startLine=`sed -n '/##donotdeletethelinestart##/=' uninstall.sh`
  local endLine=`sed -n '/##donotdeletethelineend##/=' uninstall.sh`

  sed -i $startLine','$endLine'd' uninstall.sh

  echo 'rm -rf $0' >> uninstall.sh

  local FILE_SIZE=`stat --format "%s" antiddos.bundle`

  mkdir -p antiddos/install
  mv uninstall.sh antiddos/install/
  cp -r linux/src/install/* antiddos/install/
  mkdir -p antiddos/lib
  cp -r ./bin/antiddos.tar ./antiddos/lib/
  cp -r ./bin/mysql/ ./antiddos/lib/
  cp -r ./bin/version ./antiddos/  
  tar cf antiddos.tar antiddos/  
  gzip antiddos.tar  
  cat antiddos.tar.gz >> antiddos.bundle

  local a=`expr $FILE_SIZE % 256`
  local b=`expr $FILE_SIZE / 256 % 256`
  local c=`expr $FILE_SIZE / 256 / 256 % 256`
  local d=`expr $FILE_SIZE / 256 / 256 / 256 % 256`


  echo -e "\x`printf %.2x $a`\x`printf %.2x $b`\x`printf %.2x $c`\x`printf %.2x $d`\c" >> antiddos.bundle  
  echo -e '\x96\x97\x98\x99\c' >> antiddos.bundle
  rm -rf antiddos/
  rm -rf antiddos.tar*
  mv antiddos.bundle antiddos_$ANTIVERSION.$TIME_BUILD.bundle
  chmod +x antiddos_$ANTIVERSION.$TIME_BUILD.bundle
  md5sum antiddos_$ANTIVERSION.$TIME_BUILD.bundle > antiddos_$ANTIVERSION.$TIME_BUILD.bundle.md5 
  mv antiddos_$ANTIVERSION.$TIME_BUILD.bundle* ../rpm/
  
  echo "Build anti ddos bundle...done"
  
  return 
}

makeimagedata()
{
  if [ ! -f bin/antiddos.tar ]; then    
    docker save -o bin/antiddos.tar antiddos
  fi 


  echo "Saving antiddosdata..."
  docker save -o bin/antiddosdata.tar antiddosdata
  echo "Saving antiddosdata...done"

  echo "Backup antiddos.tar..."
  mkdir -p bin/bak
  cp -r ./bin/antiddos.tar ./bin/bak/
  echo "Backup antiddos.tar...done"

  echo "xz antiddos..."
  xz ./bin/antiddos.tar
  echo "xz antiddos...done"
  echo "xz antiddosdata..."
  xz ./bin/antiddosdata.tar
  echo "xz antiddosdata...done"

  mv -f ./bin/bak/antiddos.tar ./bin/
  rm -rf ./bin/bak/

  return  
}


makebooteriso()
{
  createrDockerfileboot2iso
  createbooterstart
  chmod +x bin/antiddos
  makeimagedata


  docker build -t anti-boot2docker-img -f ./bin/Dockerfile.boot2iso .
  docker run --rm anti-boot2docker-img > ./bin/boot2docker.iso
  docker rmi anti-boot2docker-img

  rm -rf bin/antiddos bin/Dockerfile.boot* bin/*.xz

  return 
}

makeexe()
{
  local DOCKER_WINDOWS_IMAGE=windows-installer
  local DOCKER_WINDOWS_CONTAINER=build-windows-installer
  makebooteriso
  createDockerfilemakeexe

  docker rm $DOCKER_WINDOWS_CONTAINER 2>/dev/null || true
  docker build -t $DOCKER_WINDOWS_IMAGE -f ./bin/Dockerfile.makeexe .
  sleep 5 
  docker run --name $DOCKER_WINDOWS_CONTAINER $DOCKER_WINDOWS_IMAGE /bin/bash
  docker cp $DOCKER_WINDOWS_CONTAINER:/installer/Output/antiddos.exe ./bin/

  docker rm $DOCKER_WINDOWS_CONTAINER 2>/dev/null || true
  docker rmi $DOCKER_WINDOWS_IMAGE 2>/dev/null || true
  mv ./bin/antiddos.exe ../rpm/antiddos_$ANTIVERSION.$TIME_BUILD.exe  
  rm -rf ./bin/Dockerfile.makeexe
  rm -rf ./bin/*.iso

  return 
}



cleanall()
{
  rm -rf ./bin/
  return 
}


preallaction()
{
  cleanall;
  replace;
  buildcode;
  getmysqlconf;
  createrpmfie;
  createrpmdir;

  return
}

preall()
{
  preallaction;

  return 
}

usage() 
{
    echo "Usage: bash $0 {all|linux|windows|ubuntu|centos|bundle}"
    exit 1
}

main()
{
  case "$1" in
    linux)
      echo "Building all linux: deb, rpm, bundle..."
      preall;
      buildrpm;
      buildbundle;
      #cleanall;
      exit 0 
      ;;
    windows)
      echo "Building windows: exe..."
      preall;
      makeexe;
      #cleanall;
      exit 0
      ;;
    ubuntu)
      echo "Building deb..."
      preall;
      builddebonly;
      #cleanall;
      exit 0
      ;;

    centos)
      echo "Building rpm..."
      preall;
      buildrpmonly;
      #cleanall;
      exit 0
      ;;

    bundle)
      echo "Building bundle..."
      preall;
      buildbundle;
      #cleanall;
      exit 0
      ;;
    all)
      echo "Building all linux: deb, rpm, bundle, exe..."
      preall;
      buildrpm;
      buildbundle;
      makeexe;
      #cleanall;
      exit 0
      ;;
    *)
      usage >&2
      ;;

  esac
}

main "$@"

exit 0

