curl -X POST \
-d '{
"filter": {
  "from": {
  	"date": "17-04-2019",
  	"time": "00:00"
  },
  "to": {
    "date": "17-04-2019",
    "time": "23:59"
  }
},
  "pageNum": 1,
  "pageSize": 100
}' \
-H "content-type: application/json" \
-o accelq.json \
http://172.16.30.73/awb/api/optanix/seansville/v1/ext-access/runs
