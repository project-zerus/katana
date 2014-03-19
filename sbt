#!/usr/bin/env sh
java -XX:MaxPermSize=4096M -Xmx4096M -jar sbt-launch.jar "$@"
