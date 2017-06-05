package com.youmeek.elasticsearch;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class BaseDemo {
	private static final Logger logger = LogManager.getLogger(BaseDemo.class);

	@SuppressWarnings({"unchecked", "resource"})
	public static void main(String[] args) throws IOException {
		// 先构建client，两个参数分别是：cluster.name 固定参数代表后面参数的含义，集群名称
		Settings settings = Settings.builder().put("cluster.name", "youmeek-cluster").build();

		TransportClient transportClient = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.127"), 9300));

		create(transportClient);
		update(transportClient);
		query(transportClient);
		delete(transportClient);

		transportClient.close();
	}


	/**
	 * 创建
	 *
	 * @param transportClient
	 */
	private static void create(TransportClient transportClient) throws IOException {
		IndexResponse indexResponse = transportClient.prepareIndex("product_index", "product", "1").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-1")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse2 = transportClient.prepareIndex("product_index", "product", "2").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-2")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse3 = transportClient.prepareIndex("product_index", "product", "3").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-3")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse4 = transportClient.prepareIndex("product_index", "product", "4").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-4")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse5 = transportClient.prepareIndex("product_index", "product", "5").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-5")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

	}

	/**
	 * 获取单个对象
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void query(TransportClient transportClient) throws IOException {
		GetResponse getResponse = transportClient.prepareGet("product_index", "product", "1").get();
		logger.info("--------------------------------：" + getResponse.getSourceAsString());
	}

	/**
	 * 修改
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void update(TransportClient transportClient) throws IOException {
		UpdateResponse updateResponse = transportClient.prepareUpdate("product_index", "product", "1")
				.setDoc(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700 促销优惠")
						.endObject())
				.get();
		logger.info("--------------------------------：" + updateResponse.getResult());

	}

	/**
	 * 删除
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void delete(TransportClient transportClient) throws IOException {
		DeleteResponse deleteResponse = transportClient.prepareDelete("product_index", "product", "1").get();
		logger.info("--------------------------------：" + deleteResponse.getResult());
	}

	//============================================================================================================

	/**
	 * 多个条件查询
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryMore(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.matchQuery("product_name", "飞利浦"))
				.setPostFilter(QueryBuilders.rangeQuery("price").from(300).to(400))
				.setFrom(0).setSize(1)
				.get();

		SearchHit[] searchHits = searchResponse.getHits().getHits();
		for (int i = 0; i < searchHits.length; i++) {
			logger.info("--------------------------------：" + searchHits[i].getSourceAsString());
		}
	}

	//============================================================================================================

	/**
	 * 聚合分析
	 * 1. 先分组
	 * 2. 子分组
	 * 3. 最后算出子分组的平均值
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void aggregate(TransportClient transportClient) throws IOException {

		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.addAggregation(AggregationBuilders.terms("product_group_by_price").field("price")
						.subAggregation(AggregationBuilders.dateHistogram("product_group_by_created_date_time").field("created_date_time")
								.dateHistogramInterval(DateHistogramInterval.YEAR)
								.subAggregation(AggregationBuilders.avg("product_avg_price").field("price")))
				).execute().actionGet();

		Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();

		StringTerms productGroupByPrice = (StringTerms) aggregationMap.get("product_group_by_price");
		Iterator<Terms.Bucket> productGroupByPriceIterator = productGroupByPrice.getBuckets().iterator();
		while (productGroupByPriceIterator.hasNext()) {
			Terms.Bucket productGroupByPriceBucket = productGroupByPriceIterator.next();
			logger.info("--------------------------------：" + productGroupByPriceBucket.getKey() + ":" + productGroupByPriceBucket.getDocCount());

			Histogram productGroupByPrice1 = (Histogram) productGroupByPriceBucket.getAggregations().asMap().get("product_group_by_price");
			Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByCreateDateTimeIterator = productGroupByPrice1.getBuckets().iterator();
			while (groupByCreateDateTimeIterator.hasNext()) {
				Histogram.Bucket groupByCreateDateTimeBucket = groupByCreateDateTimeIterator.next();
				logger.info("--------------------------------：" + groupByCreateDateTimeBucket.getKey() + ":" + groupByCreateDateTimeBucket.getDocCount());

				Avg avgPrice = (Avg) groupByCreateDateTimeBucket.getAggregations().asMap().get("product_avg_price");
				logger.info("--------------------------------：" + avgPrice.getValue());
			}
		}


	}


}
