# 博文内容

- 博文标题：1.6 Elasticsearch 乐观锁
- 博文地址：<http://www.youmeek.com/elasticsearch-introduction-and-install/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>

------------------------


## 数据准备

- 先删除前面章节的索引：`DELETE /product_index?pretty`
- 创建带有 Tags 的索引数据：

``` json
PUT /product_index/product/111
{
  "product_name": "PHILIPS toothbrush HX6730/02",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}

PUT /product_index/product/222
{
  "product_name": "PHILIPS toothbrush HX6730/02",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "custom_version": 1,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

## 乐观锁相关概念


## 乐观锁操作

### Elasticsearch 默认的 _version 控制

- 核心：
	- Elasticsearch 内部的 _version 只要等于当前数据存储的 _version 值即可修改成功。

- 下面是全量更新的操作测试：
- 客户端 1 执行：

``` json
PUT /product_index/product/111?version=1
{
  "product_name": "PHILIPS toothbrush HX6730/02 update1",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 执行

``` json
PUT /product_index/product/111?version=1
{
  "product_name": "PHILIPS toothbrush HX6730/02 update2",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 会执行失败，因为 version 已经不等于 1 了，这时候需要重新 GET 一次获取到最新的数据，然后再重新带上最新的 version 值进行更新。



### Elasticsearch 自定义的 version 控制

- 核心：
	- Elasticsearch 内部的 _version 只要大于当前数据存储的 _version 值即可（不能等于）。

- 客户端 1 执行：

``` json
PUT /product_index/product/222?version=3&version_type=external
{
  "product_name": "PHILIPS toothbrush HX6730/02 update3",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 执行：

``` json
PUT /product_index/product/222?version=5&version_type=external
{
  "product_name": "PHILIPS toothbrush HX6730/02 update5",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- partial update 更新方式内置乐观锁并发控制



















## 其他资料辅助




