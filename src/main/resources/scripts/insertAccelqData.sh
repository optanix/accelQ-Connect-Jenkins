#!/bin/sh
version=006

curl -X POST \
  --data-binary @accelq.json \
  -H "content-type: application/json" \
  http://localhost:9200/accelq${version}/19415/?
