#!/bin/bash

. "$( dirname "${BASH_SOURCE[0]}" )/env.sh"

if [ -f "$JSVC_PID_FILE" ]; then
  echo "$DEAMON_NAME Daemon is already running. ($( cat "$JSVC_PID_FILE" ))" >&2
  exit 1
fi

echo 'Starting Daemon $DEAMON_NAME in Background.'

$JSVC_EXECUTABLE -server -cp "$JAVA_CLASSPATH" -user "$JSVC_USER" \
  -pidfile $JSVC_PID_FILE -procname "kvstore" $JAVA_OPTS $JAVA_MAIN_CLASS