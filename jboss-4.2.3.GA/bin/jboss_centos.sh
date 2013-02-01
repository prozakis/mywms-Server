#!/bin/sh
 
### BEGIN INIT INFO
# Provides:          jboss
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start and stop JBoss AS
# Description:       Enable JBoss AS service provided by daemon.
### END INIT INFO
 
ECHO=/bin/echo
TEST=/usr/bin/test
JBOSS_USER=Parhs
JBOSS_IP=127.0.0.1
JBOSS_HOME=/home/Parhs/Documents/Programming/Java/mywms/jboss-4.2.3.GA
JBOSS_START_SCRIPT=$JBOSS_HOME/bin/run.sh
JBOSS_STOP_SCRIPT=$JBOSS_HOME/bin/shutdown.sh
 
$TEST -x $JBOSS_START_SCRIPT || exit 0
$TEST -x $JBOSS_STOP_SCRIPT || exit 0
 
start() {
    $ECHO -n "Starting JBoss"
    su - $JBOSS_USER -c "$JBOSS_START_SCRIPT -b $JBOSS_IP > /dev/null &"
    $ECHO "."
}
 
stop() {
    $ECHO -n "Stopping JBoss"
    su - $JBOSS_USER -c "$JBOSS_STOP_SCRIPT -s $JBOSS_IP > /dev/null &"
    while [ "$(ps -fu $JBOSS_USER | grep java | grep jboss | wc -l)" -gt "0" ]; do
        sleep 5; $ECHO -n "."
    done
    $ECHO "."
}
 
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        sleep 30
        start
        ;;
    *)
        $ECHO "Usage: jboss {start|stop|restart}"
        exit 1
esac
exit 0
