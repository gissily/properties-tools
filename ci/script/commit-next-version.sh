#!/bin/bash

set -e

SCRIPT=`readlink -f "${BASH_SOURCE:-$0}"`
SCRIPT_DIR_PATH=`dirname ${SCRIPT}`
CI_DIR_PATH=`dirname ${SCRIPT_DIR_PATH}`
ROOT_PATH=`dirname ${CI_DIR_PATH}`


# tag release
SNAPSHOT_VERSION=$(${ROOT_PATH}/mvnw help:evaluate -Dexpression=project.version | grep "^[^\\[]" |grep -v Download)
RELEASE_VERSION=$(echo ${SNAPSHOT_VERSION} | cut -d '-' -f 1)

${ROOT_PATH}/mvnw versions:set -DnewVersion=${RELEASE_VERSION}
${SCRIPT_DIR_PATH}/clean-pom.sh

TAG_NAME=${RELEASE_VERSION}.release
git tag -a ${TAG_NAME} -m "Release Tag ${RELEASE_VERSION}"
git push origin ${TAG_NAME}

${ROOT_PATH}/mvnw versions:set -DnewVersion=${SNAPSHOT_VERSION}

# next version
${SCRIPT_DIR_PATH}/next-version.sh -${VERSION_DIRECTION:-s}

${SCRIPT_DIR_PATH}/clean-pom.sh

NEXT_DEVELOPMENT_VERSION=$(cat /tmp/NEXT_DEVELOPMENT_VERSION)

# commit
git add .
git commit -m "Next development version (${NEXT_DEVELOPMENT_VERSION})"
git push