#!/bin/bash

DAEMON_NAME=online-status-server

JSVC_EXECUTABLE="$( which jsvc )"
JSVC_PID_FILE="/tmp/$DAEMON_NAME.pid"

if [ -z "$JSVC_USER" ]; then
  JSVC_USER="$USER"
fi

DIST_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )"
LIB_DIR="$DIST_DIR/lib"
CONF_DIR="$DIST_DIR/conf"

JAVA_EXEC="$( which java )"
JAVA_CLASSPATH="$LIB_DIR/$DAEMON_NAME.jar"
JAVA_MAIN_CLASS="cn.edu.sjtu.se.dclab.oss.OSSDaemon"
JAVA_OPTS="-Ddistribution.dir=$DIST_DIR"

# set JAVA_HOME to your directory.
JAVA_HOME=/home/francis/softwares/jdk1.8.0_45

export JSVC_EXECUTABLE JSVC_PID_FILE JSVC_USER DIST_DIR CONF_DIR JAVA_EXEC \
  JAVA_CLASSPATH JAVA_MAIN_CLASS DAEMON_NAME
