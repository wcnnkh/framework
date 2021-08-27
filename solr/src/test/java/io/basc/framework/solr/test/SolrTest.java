package io.basc.framework.solr.test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.SplitLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.util.NamedList;

/**
 * solr安装教程 {@link https://solr.apache.org/guide/8_8/solr-tutorial.html}<br/>
 * solrj使用教程 {@link https
 * ://solr.apache.org/guide/8_8/using-solrj.html#using-solrj}<br/>
 * 
 * @author shuchaowen
 *
 */
public class SolrTest {
	private static Logger logger = LoggerFactory.getLogger(SolrTest.class);
	private static List<String> hosts = Arrays
			.asList("http://localhost:8983/solr");
	private static SolrClient client = new CloudSolrClient.Builder(hosts)
			.build();
	
	private static String collections = "gettingstarted";

	public void query() throws SolrServerException, IOException {
		logger.info(new SplitLine("query").toString());
		Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", "*");
		queryParamMap.put("fl", "id, name");
		queryParamMap.put("sort", "id asc");

		MapSolrParams mapSolrParams = new MapSolrParams(queryParamMap);

		QueryResponse response = client.query(collections, mapSolrParams);
		
		SolrDocumentList documents = response.getResults();
		logger.info("Found " + documents.getNumFound() + " documents");
		for (SolrDocument document : documents) {
			String id = (String) document.getFirstValue("id");
			String name = (String) document.getFirstValue("name");
			logger.info("id: " + id + ": name: " + name);
		}
	}
	
	public void addBean() throws SolrServerException, IOException{
		logger.info(new SplitLine("addBean").toString());
		TechProduct kindle = new TechProduct("kindle-id-4", "Amazon Kindle Paperwhite");
		UpdateResponse response = client.addBean(collections, kindle);
		client.commit(collections);
		logger.info(response.toString());
	}
	
	public void queryBeans() throws SolrServerException, IOException{
		logger.info(new SplitLine("queryBeans").toString());
		final SolrQuery query = new SolrQuery("*:*");
		query.addField("id");
		query.addField("name");
		query.setSort("id", ORDER.asc);
		
		QueryResponse response = client.query(collections, query);
		List<TechProduct> products = response.getBeans(TechProduct.class);
		for (TechProduct product : products) {
			logger.info("id: " + product.id + ": name: " + product.name);
		}
	}
	
	public class TechProduct {
		@Field
		public String id;
		@Field
		public String name;

		public TechProduct(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public TechProduct() {
		}
	}

	public void other() throws SolrServerException, IOException{
		logger.info(new SplitLine("other").toString());
		@SuppressWarnings({"rawtypes"})
		final SolrRequest request = new CollectionAdminRequest.ClusterStatus();

		final NamedList<Object> response = client.request(request);
		@SuppressWarnings({"unchecked"})
		final NamedList<Object> cluster = (NamedList<Object>) response.get("cluster");
		@SuppressWarnings({"unchecked"})
		final List<String> liveNodes = (List<String>) cluster.get("live_nodes");

		logger.info("Found " + liveNodes.size() + " live nodes");
	}
}
