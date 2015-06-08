#!/bin/bash

rm -rf online-status-server
mvn clean package
unzip target/online-status-server.zip -d .
