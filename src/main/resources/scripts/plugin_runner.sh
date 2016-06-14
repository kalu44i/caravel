#!/bin/bash

plugin_dir=${install.dir}
insightd_jar=${insightd.dir}/insightd-${insightd.version}.jar

platform="$(uname -s)"
arch="$(uname -m)"

if [ platform = "unknown" ]; then
  platform='Linux'
fi
if [ uname = "unknown" ]; then
  arch='x86-64'
fi

NATIVEPATH=$plugin_dir/native/$platform/$arch
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8 -Djava.libarary.name=$NATIVEPATH"
CLASSPATH=$CLASSPATH:$insightd_jar:$plugin_dir/*:$plugin_dir:/etc/insight/drivers/*:/etc/insight/drivers

java $JAVA_OPTS -cp "$CLASSPATH" ${plugin.main.class} "$@"
