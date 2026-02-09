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

RELEASE_KEY="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/Release.asc"
DOWNLOAD_URL="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/props-${BIN_SUFFIX}"
DOWNLOAD_URL_ASC="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/props-${BIN_SUFFIX}.asc"

echo "downloading ${RELEASE_KEY}"
curl -L "${RELEASE_KEY}" -o /tmp/Release.asc

echo "downloading ${DOWNLOAD_URL}"
curl -L "${DOWNLOAD_URL}" -o /tmp/props-${BIN_SUFFIX}

echo "downloading ${DOWNLOAD_URL_ASC}"
curl -L "${DOWNLOAD_URL_ASC}" -o /tmp/props-${BIN_SUFFIX}.asc

gpg --import /tmp/Release.asc
gpg --verify /tmp/props-${BIN_SUFFIX}.asc /tmp/props-${BIN_SUFFIX}

chmod +x /tmp/props-${BIN_SUFFIX}

cp /tmp/props-${BIN_SUFFIX} /usr/local/bin/props

rm -f /tmp/Release.asc
rm -f /tmp/props-${BIN_SUFFIX}
rm -f /tmp/props-${BIN_SUFFIX}.asc