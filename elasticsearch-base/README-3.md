# 博文内容

- 博文标题：1.3 Elasticsearch Document（文档）的管理
- 博文地址：<http://www.youmeek.com/elasticsearch-introduction-and-install/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>

------------------------

## Document 介绍

- Document 是 Elasticsearch 最小的数据单元，可以理解为数据库结构中的一行数据。
- Document 的数据格式是用 JSON 来表示的，如下：

``` json
{
  "email": "gitnavi@qq.com",
  "login_name": "youmeek",
  "card_info": {
    "card_name": "zhangsan",
    "card_num": "350111199002205317"
  },
  "create_date": "2017-05-30 12:04:54"
}
```

- 用 JSON 的好处是可以表达复杂的对象结构，比如一个对象中含有另外一个子对象，子对象中还有集合的子子对象等，也符合当前业界的易用的数据交流。

## Document 的简单 CURD

- 新增 3 条 Document（如果没有 Index 和 Type 会自动提前在创建）：

``` json
PUT /product_index/product/1
{
    "product_name" : "PHILIPS toothbrush HX6730/02",
    "product_desc" :  "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
    "price" :  399.00
}

PUT /product_index/product/2
{
    "product_name" : "Braun toothbrush 2000 3D",
    "product_desc" :  "6 月 1 日 16 点秒杀，仅 329 元！限量 1000 支，抢完即止！带压力感应提醒，保护牙龈，高效清洁",
    "price" :  499.00
}

PUT /product_index/product/3
{
    "product_name" : "iphone7 shell",
    "product_desc" :  "一说到星空，就有太多美好的记忆，美丽的浩瀚宇宙，有太多说不清的神秘之处，星空太美丽，太绚烂！",
    "price" :  36.00
}

## POST 方式新增数据，不指向 ID 会自动生成一个 20 位的字符串 ID。
POST /product_index/product
{
    "product_name" : "iphone6 shell",
    "product_desc" :  "真正 360° 全包边 防指纹防摔",
    "price" :  28.00
}
```

- 查询/检索 Document：
	- 通过 ID 查询（默认返回所有元数据）：`GET /product_index/product/3`
	- 通过 ID 查询（返回指定元数据）：`GET /product_index/product/3?_source=product_name,product_desc`
	- 查询所有：`GET /product_index/product/_search`
	- 通过商品名搜索，并价格倒序：`GET /product_index/product/_search?q=product_name:toothbrush&sort=price:desc`

- 更新整个 Document（需要带上所有属性，注意细节，这里改了 product_name）：
- 这种方式的本质是：软删除。把旧版本标记为 deleted，实际还没物理删除，该条数据的 _version 元数据其实会再 +1 的。如果你再 PUT 下还是这个 ID 数据进去，_version 还是会继续 +1。当 Elasticsearch 数据越来越多，会物理删除这些标记的数据。

``` json
PUT /product_index/product/3
{
    "product_name" : "星空太空 iphone7 plus 蓝紫色 6s 繁星 7plus 宇宙 se 原创保护苹果 5 包手机壳",
    "product_desc" :  "一说到星空，就有太多美好的记忆，美丽的浩瀚宇宙，有太多说不清的神秘之处，星空太美丽，太绚烂！",
    "price" :  36.00
}
```

- 更新 Document 其中一个 field：（这种叫做：partial update，整个流程本质跟全量更新差不多，都是先把旧的标记为 deleted，新的数据创建。只是查询这个过程是在 Elasticsearch 内部，效率更高。）

``` json
POST /product_index/product/3/_update
{
  "doc": {
    "product_name": "星空太空 iphone7 蓝紫色 6s 繁星 iphone7 plus 宇宙 se 原创保护苹果 5 包手机壳"
  }
}
```

- 删除 Document：
	- 通过 ID 删除：`DELETE /product_index/product/3`，本质：标记为 deleted，等数据越来越大的情况 Elasticsearch 才去物理删除。


## Document 操作返回结果的几个重要参数讲解

- 查询所有 Document：`GET /product_index/product/_search`，返回结果：

``` json
{
  "took": 3, ## 表示查询花费的时间（单位毫秒）
  "timed_out": false, ## 是否超时
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 3, ## 查询结果的数量
    "max_score": 1, ## 最大相关数分数是 1
    "hits": [ ## 查询结果的详细数据集合
      {
        "_index": "product_index",
        "_type": "product",
        "_id": "2",
        "_score": 1,
        "_source": {
          "product_name": "Braun toothbrush 2000 3D",
          "product_desc": "6 月 1 日 16 点秒杀，仅 329 元！限量 1000 支，抢完即止！带压力感应提醒，保护牙龈，高效清洁",
          "price": 499
        }
      },
      {
        "_index": "product_index",
        "_type": "product",
        "_id": "1",
        "_score": 1,
        "_source": {
          "product_name": "PHILIPS toothbrush HX6730/02",
          "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
          "price": 399
        }
      },
      {
        "_index": "product_index",
        "_type": "product",
        "_id": "3",
        "_score": 1,
        "_source": {
          "product_name": "iphone7 shell",
          "product_desc": "一说到星空，就有太多美好的记忆，美丽的浩瀚宇宙，有太多说不清的神秘之处，星空太美丽，太绚烂！",
          "price": 36
        }
      }
    ]
  }
}
```




## 其他资料辅助




