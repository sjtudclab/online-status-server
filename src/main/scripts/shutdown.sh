#!/bin/bash

. "$( dirname "${BASH_SOURCE[0]}" )/env.sh"

if [ ! -f "$JSVC_PID_FILE" ]; then
  echo "Daemon not running." >&2
  exit 1
fi

echo 'Stopping Daemon $DEAMON_NAME...'

$JSVC_EXECUTABLE -server -stop -cp "$JAVA_CLASSPATH" -user "$JSVC_USER" \
  -pidfile $JSVC_PID_FILE $JAVA_OPTS $JAVA_MAIN_CLASS