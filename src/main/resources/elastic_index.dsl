PUT /shop
{
 "settings": {
   "number_of_shards": 1,
   "number_of_replicas": 1
 },
 "mappings": {
   "properties": {
     "id": {"type": "integer"},
     "name": {"type": "text", "analyzer": "ik_max_word", "search_analyzer": "ik_smart"},
     "tags": {"type": "text", "analyzer": "whitespace", "fielddata": true},
     "location": {"type": "geo_point"},
     "remark_score": {"type": "double"},
     "price_per_man": {"type": "integer"},
     "category_id": {"type": "integer"},
     "category_name": {"type": "keyword"},
     "seller_id": {"type": "integer"},
     "seller_remark_score": {"type": "double"},
     "seller_disabled_flag": {"type": "integer"}
   }
 }
}