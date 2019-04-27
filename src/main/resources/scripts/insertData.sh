#!/bin/sh
version=15

curl -X POST \
  --data-binary @data.json \
  -H "content-type: application/json" \
  http://localhost:9200/company${version}/employee/?
#  http://localhost:9200/company${version}/employee/?_create

#  -d '{ "name" : "Andrew" },
#      { "age"  : 45       },
#      { "experienceInYears" : 10 }' \

