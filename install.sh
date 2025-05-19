#!/bin/bash

set -e

OSTYPE=$(uname)
BIN_SUFFIX=""

if [[ "${OSTYPE}" == "Darwin" ]]; then
  BIN_SUFFIX="macos"
else 
  BIN_SUFFIX="linux"
fi

RELEASE=$(curl -s https://api.github.com/repos/gissily/properties-tools/releases/latest  | grep tag_name | cut -d '"' -f 4)

if [[ "${RELEASE}" == "" ]]; then
  echo "fetching latest version failure"
  exit 1
fi

DOWNLOAD_URL="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/props-${BIN_SUFFIX}"
 
echo "downloading ${DOWNLOAD_URL}"

curl -L ${DOWNLOAD_URL} -o /tmp/props 

chmod +x /tmp/props

cp /tmp/props /usr/local/bin/props

rm -f /tmp/props