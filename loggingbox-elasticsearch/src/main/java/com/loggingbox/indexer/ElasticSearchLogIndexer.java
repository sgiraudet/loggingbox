package com.loggingbox.indexer;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import com.log.model.Level;
import com.log.model.Log;
import com.log.model.command.Search;
import com.log.model.result.SearchResult;
import com.log.storage.LogIndexer;

@Component
public class ElasticSearchLogIndexer implements LogIndexer {

	private static final Logger LOGGER = Logger
			.getLogger(ElasticSearchLogIndexer.class);

	private final static String INDEX = "logging_box";
	private final static String LOG = "log";

	private final static String LOG_SOURCE = "{\"log\":{"
			+ "\"properties\" : {" + "\"id\" : {\"index\" : \"not_analyzed\"}"
			+ "\"data\" : {\"index\" : \"\"}" + "} " + "}";

	private final static String FIELD_ID = "id";
	private final static String FIELD_APPLICATION_ID = "applicationId";
	private final static String FIELD_HOST = "host";
	private final static String FIELD_LEVEL = "level";
	private final static String FIELD_TYPE = "type";
	private final static String FIELD_DATE = "date";
	private final static String FIELD_DATA = "data";

	private Client client;

	private synchronized final Client getClient() {
		if (client == null) {

			Settings settings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", "elasticsearch")
					.put("multicast.enabled", false).put("node.master", true)
					.put("index.number_of_shards", 1)
					.put("index.number_of_replicas", 0)
					.put("transport.tcp.port", 9350)
					.put("client.transport.sniff", false).build();

			Node node = nodeBuilder().settings(settings).local(true).node();
			client = node.client();

			NodesInfoResponse rsp = client.admin().cluster()
					.nodesInfo(new NodesInfoRequest()).actionGet();
			String str = "Cluster:" + rsp.getClusterName() + ". Active nodes:";
			str += rsp.getNodesMap().keySet();
			System.out.println(str);

			client.admin().cluster().prepareHealth().setWaitForGreenStatus()
					.execute().actionGet();
			checkIndex();
		}
		return client;
	}

	private void checkIndex() {
		if (!getClient().admin().indices().prepareExists(INDEX).execute()
				.actionGet().exists()) {

			getClient().admin().indices().prepareCreate(INDEX).execute()
					.actionGet();
			getClient().admin().indices().preparePutMapping(INDEX)
					.setIgnoreConflicts(true).setType(LOG)
					.setSource(LOG_SOURCE).execute().actionGet();
		}
	}

	@Override
	public void indexLog(Log log) {
		try {
			

			getClient().prepareIndex(INDEX, LOG, log.getId())
					.setSource(getContentBuilder(log)).execute().actionGet();
		} catch (IOException ex) {
			LOGGER.error("Failed to index log", ex);
		}

	}

	@Override
	public void indexLogs(List<Log> logs) {
		try {
			BulkRequestBuilder bulkBuilder = getClient().prepareBulk();

			for (Log log : logs) {
				bulkBuilder = bulkBuilder.add(getClient().prepareIndex(INDEX, LOG,
						log.getId()).setSource(getContentBuilder(log)));
			}

			bulkBuilder.execute().actionGet();
		} catch (IOException ex) {
			LOGGER.error("Failed to index logs", ex);
		}
	}
	
	private XContentBuilder getContentBuilder(Log log) throws IOException {
		XContentBuilder contentBuilder = jsonBuilder().startObject();

		contentBuilder = contentBuilder.field(FIELD_APPLICATION_ID,
				log.getApplicationId());

		contentBuilder = contentBuilder.field(FIELD_DATE, log.getDate()
				.getTime());
		contentBuilder = contentBuilder.field(FIELD_DATA, log.getData());
		if (log.getHost() != null) {
			contentBuilder = contentBuilder
					.field(FIELD_HOST, log.getHost());
		}
		if (log.getType() != null) {
			contentBuilder = contentBuilder
					.field(FIELD_TYPE, log.getType());
		}
		if (log.getLevel() != null) {
			contentBuilder = contentBuilder.field(FIELD_LEVEL, log
					.getLevel().toString());
		}
		contentBuilder = contentBuilder.field(FIELD_ID, log.getId())
				.endObject();
		return contentBuilder;
	}

	@Override
	public SearchResult searchLogs(Search search) {
		List<Log> logs = new ArrayList<Log>();

		QueryBuilder qb = QueryBuilders
				.termQuery(FIELD_DATA, search.getToken().toLowerCase());

		qb = QueryBuilders.filteredQuery(
				qb,
				FilterBuilders.termFilter(FIELD_APPLICATION_ID,
						search.getApplicationId()));

		LOGGER.info(String.format("{%s} Search query {%s} {%s, %s}  ",
				search.getApplicationId(), search.getToken(), search.getFrom(), search.getSize()));

		SearchResult searchResult = new SearchResult();
		try {
			SearchResponse response = getClient()
					.prepareSearch(INDEX)
					.setTypes(LOG)
					.addFields(FIELD_DATA, FIELD_DATE, FIELD_HOST, FIELD_LEVEL,
							FIELD_TYPE)
					.setSearchType(SearchType.DFS_QUERY_AND_FETCH).setQuery(qb)
					.setFrom(search.getFrom()).setSize(search.getSize()).setExplain(true).execute()
					.actionGet();

			searchResult.setItemFounds(response.getHits().getTotalHits());
			SearchHits hits = response.getHits();
			for (SearchHit hit : hits.getHits()) {
				String logId = hit.getId();

				Log log = new Log();
				log.setId(logId);
				log.setApplicationId(search.getApplicationId());
				Map<String, SearchHitField> fields = hit.getFields();
				if (fields.containsKey(FIELD_DATE)) {
					log.setDate(new Date(fields.get(FIELD_DATE)
							.<Long> getValue()));
				}
				if (fields.containsKey(FIELD_HOST)) {
					log.setHost(fields.get(FIELD_HOST).<String> getValue());
				}
				if (fields.containsKey(FIELD_DATA)) {
					log.setData(fields.get(FIELD_DATA).<String> getValue());
				}
				if (fields.containsKey(FIELD_LEVEL)) {
					log.setLevel(Level.valueOf(fields.get(FIELD_LEVEL)
							.<String> getValue()));
				}
				if (fields.containsKey(FIELD_TYPE)) {
					log.setType(fields.get(FIELD_TYPE).<String> getValue());
				}
				logs.add(log);

			}
		} catch (Exception ex) {
			LOGGER.error(
					String.format("Failed to search term {%s}",
							search.getToken()), ex);
		}

		LOGGER.info(String.format("Find {%s} results", logs.size()));

		searchResult.setLogs(logs);
		searchResult.setApplicationId(search.getApplicationId());
		searchResult.setToken(search.getToken());
		searchResult.setFrom(search.getFrom());
		searchResult.setSize(search.getSize());
		return searchResult;
	}

}
