# curl http://localhost:9200/accelq006/19415/_search?
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits.total
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits.total,hits.max_score
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits.total,hits.max_score,hits.hits
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits.total,hits.max_score,hits.hits._source
# curl http://localhost:9200/accelq006/19415/_search?filter_path=took,timed_out,_shards,hits.total,hits.max_score,hits.hits._source.pageNum
# curl http://localhost:9200/accelq006/19415/_search?filter_path=_shards,hits.total,hits.max_score,hits.hits._source.pageNum,hits.hits._source.pageSize
# curl http://localhost:9200/accelq006/19415/_search?filter_path=hits.total,hits.hits._source.pageSize
# curl http://localhost:9200/accelq006/19415/_search?filter_path=hits.total,hits.hits._source.pageSize,hits.hits._source.data
# curl http://localhost:9200/accelq006/19415/_search?filter_path=hits.total,hits.hits._source.pageSize,hits.hits._source.data.**jobPid
curl http://localhost:9200/accelq006/19415/_search?pretty&filter_path=hits.total,hits.hits._source.pageSize,hits.hits._source.data.**jobPid,hits.hits._source.*,hits.hits._source.reportVersion
