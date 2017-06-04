# 博文内容

- 博文标题：1.4 Elasticsearch DSL 简单用法
- 博文地址：<http://www.youmeek.com/elasticsearch-introduction-and-install/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch、Kibana 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>
- Elasticsearch 和 Kibana 都要安装。后面的教程都是在 Kibana 的 Dev Tools 工具上执行的命令。

------------------------

## DSL 介绍

- 这个才是实际最常用的方式，可以构建复杂的查询条件。
- 不用一开始就想着怎样用 Java Client 端去调用 Elasticsearch 接口。DSL 会了，Client 的也只是用法问题而已。

### DSL 语句的校验以及 score 计算原理

- 对于复杂的查询，最好都先校验下，看有没有报错。

``` json
GET /product_index/product/_validate/query?explain
{
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  }
}
```


## DSL 简单用法


- 查询所有的商品：

``` json
GET /product_index/product/_search
{
  "query": {
    "match_all": {}
  }
}
```

- 查询商品名称包含 toothbrush 的商品，同时按照价格降序排序：

``` json
GET /product_index/product/_search
{
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  },
  "sort": [
    {
      "price": "desc"
    }
  ]
}
```

- 分页查询商品：

``` json
GET /product_index/product/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0, ## 从第几个商品开始查，最开始是 0
  "size": 1  ## 要查几个结果
}
```

- 指定查询结果字段（field）

``` json
GET /product_index/product/_search
{
  "query": {
    "match_all": {}
  },
  "_source": [
    "product_name",
    "price"
  ]
}
```

- 相关符号标识，官网：<https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html>

|符号标识|代表含义|
|---|---|
|gte|大于或等于|
|gt|大于|
|lte|小于或等于|
|lt|小于|

- 搜索商品名称包含 toothbrush，而且售价大于 400 元，小于 700 的商品

``` json
GET /product_index/product/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "product_name": "toothbrush"
        }
      },
      "filter": {
        "range": {
          "price": {
            "gt": 400,
            "lt": 700
          }
        }
      }
    }
  }
}
```

- full-text search 全文检索，倒排索引
- 索引中只要有任意一个匹配拆分后词就可以出现在结果中，只是匹配度越高的排越前面
- 比如查询：PHILIPS toothbrush，会被拆分成两个单词：PHILIPS 和 toothbrush。只要索引中 product_name 中只要含有任意对应单词，都会在搜索结果中，只是如果有数据同时含有这两个单词，则排序在前面。

``` json
GET /product_index/product/_search
{
  "query": {
    "match": {
      "product_name": "PHILIPS toothbrush"
    }
  }
}
```

- phrase search 短语搜索
- 索引中必须同时匹配拆分后词就可以出现在结果中
- 比如查询：PHILIPS toothbrush，会被拆分成两个单词：PHILIPS 和 toothbrush。索引中必须有同时有这两个单词的才会在结果中。


``` json
GET /product_index/product/_search
{
  "query": {
    "match_phrase": {
      "product_name": "PHILIPS toothbrush"
    }
  }
}
```

- Highlight Search 高亮搜索
- 给匹配拆分后的查询词增加高亮的 html 标签，比如这样的结果：`"<em>PHILIPS</em> <em>toothbrush</em> HX6730/02"`

``` json
GET /product_index/product/_search
{
  "query": {
    "match": {
      "product_name": "PHILIPS toothbrush"
    }
  },
  "highlight": {
    "fields": {
      "product_name": {}
    }
  }
}
```


- range 用法，查询数值、时间区间：

``` json
GET /product_index/product/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 30.00
      }
    }
  }
}
```



- match 用法（与 term 进行对比）：
- 查询的字段内容是进行分词处理的，只要分词的单词结果中，在数据中有满足任意的分词结果都会被查询出来

``` json
GET /product_index/product/_search
{
  "query": {
    "match": {
      "product_name": "PHILIPS toothbrush"
    }
  }
}
```

- multi_match 用法：
- 查询 product_name 和 product_desc 字段中，只要有：toothbrush 关键字的就查询出来。

``` json
GET /product_index/product/_search
{
  "query": {
    "multi_match": {
      "query": "toothbrush",
      "fields": [
        "product_name",
        "product_desc"
      ]
    }
  }
}
```

- match_phrase 用法（与 match 进行对比）：
- 查询的字段内容是进行分词处理的，分词的单词结果中，在数据中有满足所有的分词结果都会被查询出来

``` json
GET /product_index/product/_search
{
  "query": {
    "match_phrase": {
      "product_name": "PHILIPS toothbrush"
    }
  }
}
```

- match_phrase + slop（可调节因子用法）（与 match_phrase 进行对比）：
- 查询的字段内容是进行分词处理的，分词的单词结果中，在数据中有满足所有的分词结果都会被查询出来，也可以少 1 个不匹配（通过 slop 设置可以不匹配多少个）。

``` json
GET /product_index/product/_search
{
  "query": {
    "match_phrase": {
      "product_name" : {
          "query" : "PHILIPS toothbrush",
          "slop" : 1
      }
    }
  }
}
```

- term 用法（与 match 进行对比）（查询的字段内容是不进行分词处理的，是完全匹配查询）：
- 这个一般用在不分词字段上的。所以自己设置 mapping 的时候有些不分词的时候就最好设置上。
- 一般这个不常用。设置 mapping 不分词后，用 match 效果也是一样的。

``` json
GET /product_index/product/_search
{
  "query": {
    "term": {
      "product_name": "PHILIPS toothbrush"
    }
  }
}
```

### query 和 filter 差异

- 只用 query：

``` json
GET /product_index/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "product_name": [
              "PHILIPS",
              "toothbrush"
            ]
          }
        },
        {
          "range": {
            "price": {
              "gt": 12.00
            }
          }
        }
      ]
    }
  }
}
```

- 只用 filter：

``` json
GET /product_index/product/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "range": {
          "price": {
            "gte": 30.00
          }
        }
      }
    }
  }
}
```

- query 和 filter 一起使用，看本文下面的：多搜索条件组合查询
- 官网文档：<https://www.elastic.co/guide/en/elasticsearch/guide/current/_queries_and_filters.html>
- 从搜索结果上看：
	- filter，只查询出搜索条件的数据，不计算相关度分数
	- query，查询出搜索条件的数据，并计算相关度分数，按照分数进行倒序排序
- 从性能上看：
	- filter（性能更好，无排序），无需计算相关度分数，也就无需排序，内置的自动缓存最常使用查询结果的数据
	- query（性能较差，有排序），要计算相关度分数，按照分数进行倒序排序，没有缓存结果的功能
	- filter 和 query 一起使用可以兼顾两者的特性，所以看你业务需求。

### 多搜索条件组合查询（最常用）

- bool 下包括：must（必须匹配），must_not（必须不匹配），should（没有强制匹配），filter（过滤）

``` json
GET /product_index/product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "product_name": "PHILIPS toothbrush"
          }
        }
      ],
      "should": [
        {
          "match": {
            "product_desc": "刷头"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "product_name": "HX6730"
          }
        }
      ],
      "filter": {
        "range": {
          "price": {
            "gte": 33.00
          }
        }
      }
    }
  }
}
```

- 下面还用到自定义排序。
- 排序最好别用到字符串字段上。因为字符串字段会进行分词，Elasticsearch 默认是拿分词后的某个词去进行排序，排序结果往往跟我们想象的不一样。解决这个办法是在设置 mapping 的时候，多个这个字段设置一个 fields raw，让这个不进行分词，然后查询排序的时候使用这个 raw，具体看这里：<https://www.elastic.co/guide/cn/elasticsearch/guide/current/multi-fields.html>

``` json
GET /product_index/product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "product_name": "PHILIPS toothbrush"
          }
        }
      ],
      "should": [
        {
          "match": {
            "product_desc": "刷头"
          }
        }
      ],
      "filter": {
        "bool": {
          "must": [
            {
              "range": {
                "price": {
                  "gte": 33.00
                }
              }
            },
            {
              "range": {
                "price": {
                  "lte": 555.55
                }
              }
            }
          ],
          "must_not": [
            {
              "term": {
                "product_name": "HX6730"
              }
            }
          ]
        }
      }
    }
  },
  "sort": [
    {
      "price": {
        "order": "desc"
      }
    }
  ]
}
```



## 其他资料辅助


- [elasticsearch 查询（match和term）](http://www.cnblogs.com/yjf512/p/4897294.html)

