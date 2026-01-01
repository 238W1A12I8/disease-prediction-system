#!/bin/sh
# Maven Wrapper bootstrap script
if [ -n "$JAVA_HOME" ]; then
  JAVA_HOME_BIN="$JAVA_HOME/bin/java"
else
  JAVA_HOME_BIN="java"
fi
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER="org.apache.maven.wrapper.MavenWrapperMain"

# Download wrapper jar if missing
if [ ! -e "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$WRAPPER_JAR" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
  else
    echo "Error: curl or wget required to download Maven Wrapper." >&2
    exit 1
  fi
fi

exec "$JAVA_HOME_BIN" -cp "$WRAPPER_JAR" $WRAPPER_LAUNCHER "$@"
