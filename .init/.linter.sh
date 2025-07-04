#!/bin/bash
cd /home/kavia/workspace/code-generation/swiftcalc-107101-9312d1c8/calculator_app_android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

