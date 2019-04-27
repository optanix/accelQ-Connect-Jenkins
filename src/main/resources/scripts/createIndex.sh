#/bin/sh
number=16

curl -X PUT \
  -d '{ 
"settings" : {
  "index" : {
    "number_of_shards"   : 1,
    "number_of_replicas" : 1
    },
    "analysis" : {
      "analyzer" : {
        "analyzer-name" : {
          "type"      : "custom",
          "tokenizer" : "keyword",
          "filter"    : "lowercase"
        }
      }
    },
    "mappings" : {
      "employee" : {
        "properties" : {
          "age" : {
          },
          "experienceInYears" : {
          },
          "name" : {
          }
        }
      }
    }
  }
}' \
  -H "content-Type: application/json" \
 http://localhost:9200/company${number}


