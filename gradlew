#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    expr "$link" : '/.*' > /dev/null || link=`dirname "$PRG"`"/$link"
    PRG="$link"
done
saveddir=`pwd`
CDPATH=`cd `dirname "$PRG"` && pwd`
cd "$CDPATH"
PRG_HOME=`pwd`
cd "$saveddir"

CLASSPATH=$PRG_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || {
        echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." 2>&1
        exit 1
    }
fi

# Increase the maximum heap, if we can-ish
if [ "$CYGWIN" = "true" -or "cygwin" ] ; then
    supported_versions="1.5|1.6|1.7|1.8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24"
    if java -version 2>&1 | grep -E "version \"($supported_versions)" >/dev/null; then
        DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"
    fi
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS "-Dorg.gradle.appname=gradlew" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$%@"
