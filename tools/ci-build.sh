#!/bin/bash -ex
#
# Copyright 2023 Miklos Vajna
#
# SPDX-License-Identifier: MIT
#

#
# This script runs all the tests for CI purposes.
#

mkdir -p app/keystore/
echo $KEYSTORE | base64 -d > app/keystore/plees_keystore.jks

./gradlew build
./gradlew test
./gradlew connectedAndroidTest

curl -sSLO https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint
chmod a+x ktlint
git ls-files| grep '\.kt[s"]\?$' | xargs ./ktlint --android --relative .

tools/license-check.sh

mkdir dist
cp app/build/outputs/apk/release/app-release.apk dist/

# vim:set shiftwidth=4 softtabstop=4 expandtab:
