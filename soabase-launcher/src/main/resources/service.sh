#!/bin/bash
#
# Copyright 2014 Jordan Zimmerman
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# chkconfig: 35 90 12
# description: Soabase Service Launcher
#

# Output an error message
errorMessage() {
    echo
    echo -e "\033[31m*** ERROR: $1\033[0m"
    echo
    exit 1
}

# Output a warning message
warnMessage() {
    echo -e "\033[33m*** WARNING: $1\033[0m"
}

# Output a warning message only if verbose
verboseWarnMessage() {
    if [ $VERBOSE = true ]; then
        warnMessage "$1"
    fi
}

# Output a message only if verbose
verboseMessage() {
    if [ $VERBOSE = true ]; then
        echo -e "\033[33m*** $1\033[0m"
    fi
}

# find the first JAR
# findjar <SERVICE_PATH> <BIN_PATH>
findJar() {
    JAR_FILE=`ls -t $1/*.jar 2> /dev/null | head -n 1`
    if [ -z $JAR_FILE ]; then
        JAR_FILE=`ls -t $2/*.jar 2> /dev/null | head -n 1`
    fi
}

findConfig() {
    CONFIG_FILE=`ls -t $CONFIG_PATH/*.json 2> /dev/null | head -n 1`
    if [ -z "$CONFIG_FILE" ]; then
        CONFIG_FILE=`ls -t $CONFIG_PATH/*.yaml 2> /dev/null | head -n 1`
    fi
}

initialize() {
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
      DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
      SOURCE="$(readlink "$SOURCE")"
      [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
    done
    SCRIPT_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

    if [ -z "$DEBUG" ]; then
        DEBUG="false"
    fi

    if [ -z "$VERBOSE" ]; then
        VERBOSE="false"
    fi

    if [ -z "$SERVICE_PATH" ]; then
        SERVICE_PATH="$( dirname "$SCRIPT_PATH" )"
        verboseWarnMessage "SERVICE_PATH not defined. Using $SERVICE_PATH"
    else
        SERVICE_PATH=$(cd $(dirname "$SERVICE_PATH"); pwd)
    fi

    CONFIG_PATH="$SERVICE_PATH/config"
    BIN_PATH="$SCRIPT_PATH"
    JVM_OPTIONS_FILE="$CONFIG_PATH/jvm.properties"
    ARGUMENTS_FILE="$CONFIG_PATH/arguments.txt"
    PIDFILE="$BIN_PATH/service.pid"

    findJar "$SERVICE_PATH" "$BIN_PATH"
    verboseMessage "JAR: $JAR_FILE"

    findConfig
    if [ -z "$CONFIG_FILE" ]; then
        verboseMessage "No config file"
    else
        verboseMessage "Config: $CONFIG_FILE"
    fi

    if [ -e "$ARGUMENTS_FILE" ]; then
        ARGUMENTS=`cat $ARGUMENTS_FILE`
        ARGUMENTS=`echo $ARGUMENTS`
        ARGUMENTS=${ARGUMENTS/\$CONFIG_FILE/$CONFIG_FILE}
        verboseMessage "Arguments: $ARGUMENTS"
    else
        verboseMessage "No arguments file"
        if [ -z "$CONFIG_FILE" ]; then
            ARGUMENTS=""
        else
            ARGUMENTS="server \"$CONFIG_FILE\""
            verboseMessage "Arguments: $ARGUMENTS"
        fi
    fi

    JVM_OPTIONS=""
    if [ -e "$JVM_OPTIONS_FILE" ]; then
        JVM_OPTIONS=`cat $JVM_OPTIONS_FILE`
        JVM_OPTIONS=`echo $JVM_OPTIONS`
        verboseMessage "JVM_OPTIONS: $JVM_OPTIONS"
    fi

    if [ -z "$SERVICE_USER" ]; then
        verboseWarnMessage "SERVICE_USER is not defined"
    else
        SERVICE_USER="--user $SERVICE_USER"
    fi

    if [ -z "$JAVA_HOME" ]; then
        JAVA_EXE=java
    else
        JAVA_EXE="$JAVA_HOME/bin/java"
    fi

    if [ $DEBUG = "true" ]; then
        warnMessage "DEBUG mode"
    fi
}

start() {
    if [ -f $PIDFILE ]; then
        PID=`cat $PIDFILE`
        errorMessage "already running: $PID"
    else
        if [ -z "$JAR_FILE" ]; then
            errorMessage "Could not find a JAR file to run"
        fi

        verboseMessage "Starting Service"
        if [ $DEBUG = "true" ]; then
            echo "$JAVA_EXE $JAVA_SCRIPT $JVM_OPTIONS -jar $JAR_FILE $ARGUMENTS"
        elif [ $1 = "run" ]; then
            verboseMessage "$JAVA_EXE $JVM_OPTIONS -jar $JAR_FILE $ARGUMENTS"
            "$JAVA_EXE" $JVM_OPTIONS -jar "$JAR_FILE" $ARGUMENTS
        else
            if [ $VERBOSE = true ]; then
                warnMessage "$JAVA_EXE $JVM_OPTIONS -jar $JAR_FILE $ARGUMENTS $CONFIG_FILE"
                "$JAVA_EXE" $JVM_OPTIONS -jar "$JAR_FILE" $ARGUMENTS &
            else
                "$JAVA_EXE" $JVM_OPTIONS -jar "$JAR_FILE" $ARGUMENTS > /dev/null 2>&1 &
            fi
        fi

        if ! [ $DEBUG = "true" ]; then
            sleep 5
            PID=`ps aux | grep java | grep "$JAR_FILE" | awk '{print $2}'`
            if ! [ -z "$PID" ]; then
                echo $PID>$PIDFILE
                verboseMessage "id: $PID"
            else
                errorMessage "Could not find id for process"
            fi
        fi
    fi
}

stop() {
    verboseMessage "Stopping Service"
    if [ -f $PIDFILE ]; then
        kill `cat $PIDFILE`
        rm $PIDFILE
    else
        errorMessage "Not running"
    fi
}

usage() {
cat <<END_OF_HELP
Usage: $(basename "$0") {help|start|run|stop|restart|status}

Environment variables:
    NAME            REQUIRED     DEFAULT                   DESCRIPTION
    ====            ========     =======                   ===========
    DEBUG           N            false                     If true, don't execute. Only output the command.
    VERBOSE         N            false                     If true, output to console.
    SERVICE_PATH    N            service.sh parent dir     The location for config, log, bin, etc directories.
    JAVA_HOME       N            java                      Java root directory.

Special Files:
    \$SERVICE_PATH/config/jvm.properties       file containing java.exe options (single line or 1 per line)

    \$SERVICE_PATH/config/arguments.txt        file containing application arguments (single line or 1 per line)
                                              The string \$CONFIG_FILE in your arguments file will be replaced with
                                              the path to your config file. NOTE: if not present and there is a config
                                              file, the arguments to the application are: server \$CONFIG_FILE
END_OF_HELP
}

status() {
    if [ -f $PIDFILE ]; then
        cat $PIDFILE
    else
        echo "Not running"
    fi
}

case "$1" in
  help)
        usage
        exit 1
        ;;
  start)
        initialize
        start start
        ;;
  run)
        initialize
        start run
        ;;
  stop)
        initialize
        stop
        ;;
  status)
        initialize
        status
        ;;
  restart)
        initialize
        stop
        start start
        ;;
  *)
        usage
        exit 1
esac
exit 0
