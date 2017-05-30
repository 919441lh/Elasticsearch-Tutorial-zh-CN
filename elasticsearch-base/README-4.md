# 博文内容

- 博文标题：1.4 Elasticsearch DSL 简单用法
- 博文地址：<http://www.youmeek.com/elasticsearch-introduction-and-install/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>

------------------------

## DSL 介绍

- 这个才是实际最常用的方式，可以构建复杂的查询条件

## DSL 使用案例


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


## 其他资料辅助




