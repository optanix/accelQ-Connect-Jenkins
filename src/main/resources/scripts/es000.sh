#/bin/sh

curl -X PUT \
  -d '{ 
"settings" : {
  "index" : {
    "number_of_shards" : 1
    }
  }
}' \
  -H "content-Type: application/json" \
 http://localhost:9200/company4 


#  -d '{"settings": { "index": { "number_of_shards" : 1, "number_of_replicas": 1 }, "analysis": { "analyzer": { "analyzer-name": { "type":"custom", "tokenizer": "keyword", "filter": "lowercase" }}}, "mappings" : { "employee" : { "properties" : { "age": { "type": "long" }, "experienceInYears" : { "type": "long"}, "name": { "type": "string", "analyzer": "analyzer-name"}}}}} }' \
