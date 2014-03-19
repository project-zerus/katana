#!/usr/bin/env bash

rm -rf bin lib katana && sync
./sbt clean test package universal:package-zip-tarball && sync
tar zxf target/universal/katana-0.1-SNAPSHOT.tgz && sync
mv katana-0.1-SNAPSHOT/* . && sync
rm -rf katana-0.1-SNAPSHOT && sync
