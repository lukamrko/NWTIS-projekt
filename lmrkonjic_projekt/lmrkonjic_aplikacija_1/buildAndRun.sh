#!/bin/sh
mvn clean package && docker build -t org.foi.nwtis.lmrkonjic/lmrkonjic_aplikacija_1 .
docker rm -f lmrkonjic_aplikacija_1 || true && docker run -d -p 9080:9080 -p 9443:9443 --name lmrkonjic_aplikacija_1 org.foi.nwtis.lmrkonjic/lmrkonjic_aplikacija_1