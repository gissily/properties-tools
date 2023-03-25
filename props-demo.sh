#!/bin/bash


set -e

SCRIPT=`readlink -f "${BASH_SOURCE:-$0}"`
ROOT_PATH=`dirname ${SCRIPT}`

PROPS=${ROOT_PATH}/target/props

file=${ROOT_PATH}/target/test-classes/test.properties

KEYS=($(${PROPS} keys ${file}))

for i in "${!KEYS[@]}"; do
  
  key=${KEYS[${i}]}
  value=$(${PROPS} value ${file} ${key})
  echo "key: ${key}, value: ${value}"

done

echo "properties file content"
cat ${file}