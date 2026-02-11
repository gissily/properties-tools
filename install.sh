#!/bin/bash

set -e

OSTYPE=$(uname)
BIN_SUFFIX=""
ARCH=""

if [[ "${OSTYPE}" == "Darwin" ]]; then
  BIN_SUFFIX="macos"
elif [[ "${OSTYPE}" == "Linux" ]]; then
  BIN_SUFFIX="linux"
else
  echo "error: The OS is not supported!"
  exit 1
fi

case "$(uname -m)" in
  'amd64' | 'x86_64')
    ARCH='x86_64'
    ;;
  'arm64' | 'aarch64')
    ARCH='arm64'
    ;;
  *)
    echo "error: The architecture is not supported!"
    exit 1
    ;;
esac

if [[ "${BIN_SUFFIX}" == "" || ${ARCH} == "" ]]; then
  echo "error: The OS or architecture is not supported!"
  exit 1
fi

RELEASE=$(curl -s https://api.github.com/repos/gissily/properties-tools/releases/latest  | grep tag_name | cut -d '"' -f 4)

if [[ "${RELEASE}" == "" ]]; then
  echo "fetching latest version failure"
  exit 1
fi

RELEASE_KEY="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/Release.asc"
DOWNLOAD_URL="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/props-${BIN_SUFFIX}-${ARCH}"
DOWNLOAD_URL_ASC="https://github.com/gissily/properties-tools/releases/download/${RELEASE}/props-${BIN_SUFFIX}-${ARCH}.asc"

echo "downloading ${RELEASE_KEY}"
curl -L "${RELEASE_KEY}" -o /tmp/Release.asc

echo "downloading ${DOWNLOAD_URL}"
curl -L "${DOWNLOAD_URL}" -o /tmp/props-${BIN_SUFFIX}

echo "downloading ${DOWNLOAD_URL_ASC}"
curl -L "${DOWNLOAD_URL_ASC}" -o /tmp/props-${BIN_SUFFIX}.asc

gpg --quiet --batch --import /tmp/Release.asc
gpg --quiet --batch --verify /tmp/props-${BIN_SUFFIX}.asc /tmp/props-${BIN_SUFFIX} 2>/dev/null

chmod +x /tmp/props-${BIN_SUFFIX}

cp /tmp/props-${BIN_SUFFIX} /usr/local/bin/props

echo "props [${RELEASE}] installed successfully!"

rm -f /tmp/Release.asc
rm -f /tmp/props-${BIN_SUFFIX}
rm -f /tmp/props-${BIN_SUFFIX}.asc
