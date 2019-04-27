#!/bin/sh
number=011

curl -X PUT \
-d '
{
  "settings" : {
    "index" : {
      "number_of_shards" : 1,
      "number_of_replicas" : 1
    },
    "analysis" : {
      "analyzer" : {
        "analyzer-name" : {
          "type" : "custom",
          "tokenizer" : "keyword",
          "filter" : "lowercase"
        }
      }
    },
    "mappings" : {
      "jobPid" : {
      }
    }
  }
}' \
-H "content-type: application/json" \
http://localhost:9200/acq${number}

