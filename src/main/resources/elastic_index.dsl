DELETE /shop

PUT /shop
{
 "settings": {
   "number_of_shards": 1,
   "number_of_replicas": 1,
   "analysis": {
     "filter": {
       "my_synonyms_filter": {
         "type": "synonym",
         "synonyms_path": "analysis-ik/synonyms.txt"
       }
     },
     "analyzer": {
       "ik_syno": {
         "type": "custom",
         "tokenizer": "ik_smart",
         "filter": ["my_synonyms_filter"]
       },
       "ik_syno_max": {
         "type": "custom",
         "tokenizer": "ik_max_word",
         "filter": ["my_synonyms_filter"]
       }
     }
   }
 },
 "mappings": {
   "properties": {
     "id": {"type": "integer"},
     "name": {"type": "text", "analyzer": "ik_syno_max", "search_analyzer": "ik_syno"},
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


GET /shop/_search
{
  "query": {
    "match": {"name": "凯悦"}
  }
}

GET /shop/_analyze
{
  "analyzer": "ik_smart",
  "text": "凯悦"
}

#带上距离字段
GET /shop/_search
{
  "query": {
    "match": {"name": "凯悦"}
  },
  "_source": "*",
  "script_fields": {
    "distance": {
      "script": {
        "source": "haversin(lat,lon,doc['location'].lat, doc['location'].lon)",
        "lang": "expression",
        "params": {"lat": 30.20, "lon": 120.20}
      }
    }
  }
}

#使用距离排序
GET /shop/_search
{
  "query": {
    "match": {"name": "凯悦"}
  },
  "_source": "*",
  "script_fields": {
    "distance": {
      "script": {
        "source": "haversin(lat,lon,doc['location'].lat, doc['location'].lon)",
        "lang": "expression",
        "params": {"lat": 30.20, "lon": 120.20}
      }
    }
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 30.20,
          "lon": 120.20
        },
        "order": "asc",
        "unit": "km",
        "distance_type": "arc"
      }
    }
  ]
}

#使用function score
GET /shop/_search
{
  "explain": false,
  "_source": "*",
  "script_fields": {
    "distance": {
      "script": {
        "source": "haversin(lat,lon,doc['location'].lat, doc['location'].lon)",
        "lang": "expression",
        "params": {"lat": 30.202123, "lon": 120.202302}
      }
    }
  },
  "query": {
    "function_score": {
      "query": {
        "bool": {
          "must": [
            {
              "bool": {
                "should": [
                  {"match": {"name": {"query": "凯悦", "boost": 0.1}}},
                  {"term": {"category_id": {"value": 2, "boost": 0.1}}}
                ]
              }
            },
            {"term": {"seller_disabled_flag": 0}}
          ]
        }
      },
      "functions": [
        {
          "gauss": {
            "location": {
              "origin": "30.202123,120.202302",
              "scale": "100km",
              "offset": "0km",
              "decay": 0.5
            }
          },
          "weight": 9
        },
        {
          "field_value_factor": {
            "field": "remark_score"
          },
          "weight": 0.2
        },
        {
          "field_value_factor": {
            "field": "seller_remark_score"
          },
          "weight": 0.1
        }
      ],
      "score_mode": "sum",
      "boost_mode": "sum"
    }
  },
  "sort": [
    {
      "_score": {
        "order": "desc"
      }
    }
  ],
  "aggs": {
    "group_by_tags": {
      "terms": {
        "field": "tags"
      }
    }
  }
}

#价格排序
GET /shop/_search
{
  "explain": false,
  "_source": "*",
  "script_fields": {
    "distance": {
      "script": {
        "source": "haversin(lat,lon,doc['location'].lat, doc['location'].lon)",
        "lang": "expression",
        "params": {"lat": 30.202123, "lon": 120.202302}
      }
    }
  },
  "query": {
    "function_score": {
      "query": {
        "bool": {
          "must": [
            {"match": {"name": {"query": "凯悦", "boost": 0.1}}},
            {"term": {"seller_disabled_flag": 0}},
            {"term": {"category_id": 2}}
          ]
        }
      },
      "functions": [
        {
          "field_value_factor": {
            "field": "price_per_man"
          },
          "weight": 1
        }
      ],
      "score_mode": "sum",
      "boost_mode": "replace"
    }
  },
  "sort": [
    {
      "_score": {
        "order": "asc"
      }
    }
  ]
}

GET /shop/_search
{

}

GET /shop/_search
{
  "query": {
    "match": {
      "name": "红桃"
    }
  }
}

POST /shop/_update_by_query
{
  "query": {
    "bool": {
      "must": [
        {"term": {"name": "凯"}},
        {"term": {"name": "悦"}}
      ]
    }
  }
}

GET /shop/_analyze
{
  "field": "name",
  "text": "凯悦"
}
