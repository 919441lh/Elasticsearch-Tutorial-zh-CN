package com.youmeek.elasticsearch;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		IndexResponse indexResponse = transportClient.prepareIndex("product_index", "product", "1")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700")
						.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
						.field("price", 399.00)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject())
				.get();
		logger.info("--------------------------------：" + indexResponse.getResult());
	}

	/**
	 * 获取
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

}
