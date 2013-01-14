package com.loggingbox.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.log.core.ApplicationRegistration;
import com.log.core.RegistrationContainer;
import com.log.export.LogExporter;
import com.log.model.Application;
import com.log.model.ApplicationObject;
import com.log.model.KpiDefinition;
import com.log.model.Log;
import com.log.model.command.Export;
import com.log.model.command.GetLogs;
import com.log.model.command.Search;
import com.log.model.result.GetLogsResult;
import com.log.storage.ApplicationAccessor;
import com.log.storage.LogAccessor;
import com.log.storage.LogIndexer;

@Component
public class LogGetServlet extends WebSocketServlet {

	private static final Logger LOGGER = Logger.getLogger(LogGetServlet.class);

	private static final long serialVersionUID = 5518097059496417942L;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	RegistrationContainer registrationContainer;

	@Autowired
	LogAccessor logAccessor;
	@Autowired
	LogIndexer logIndexer;
	@Autowired
	ApplicationAccessor applicationAccessor;
	@Autowired
	LogExporter logExporter;

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest req, String arg1) {
		return new LogWebWebSocket(req.getParameter("appId"));
	}

	private class LogWebWebSocket implements WebSocket.OnTextMessage {

		private Connection connection;
		private ApplicationRegistration applicationRegistration;
		private final String applicationId;

		public LogWebWebSocket(String applicationId) {
			this.applicationId = applicationId;
		}

		void getMessages() {
			while (connection != null && applicationRegistration != null) {
				ApplicationObject object = applicationRegistration
						.getNextObject();
				if (object != null) {
					sendMessage(object);
				}
			}
		}

		@Override
		public void onClose(int arg0, String arg1) {
			this.connection = null;
			if (applicationRegistration != null) {
				registrationContainer.unregister(applicationRegistration);
				applicationRegistration = null;
			}

		}

		private void sendMessage(ApplicationObject object) {
			if(connection == null) {
				return;
			}
			try {
				connection.sendMessage(objectMapper.writeValueAsString(object));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
			} catch (IOException e) {
				System.err.println(e);
				connection = null;
			}
		}

		@Override
		public void onOpen(Connection connection) {
			this.connection = connection;
			this.connection.setMaxIdleTime((int) TimeUnit.HOURS.toMillis(10));

			Application application = new Application();
			application.setId(applicationId);
			this.applicationRegistration = new ApplicationRegistration(
					application);

			registrationContainer.register(applicationRegistration);

			for (Application otherAppli : applicationAccessor.getApplications()) {
				sendMessage(otherAppli);
			}

			List<KpiDefinition> kpis = applicationAccessor
					.getKpiDefinitions(applicationId);
			for (KpiDefinition kpiDefinition : kpis) {
				sendMessage(kpiDefinition);
			}
			GetLogsResult oldLogs = logAccessor.getLogs(
					new GetLogs(applicationId, 500, null, false));
					
			for (int i = oldLogs.getLogs().size() - 1; i >= 0; i--) {
				Log log = oldLogs.getLogs().get(i);
				sendMessage(log);

			}
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					getMessages();

				}
			}, 0);

		}

		@Override
		public void onMessage(String data) {

			try {
				JsonNode jsonNode = objectMapper.readTree(data);
				if (jsonNode.isObject()) {
					JsonNode typeNode = jsonNode.get("objectType");
					if (typeNode != null
							&& typeNode.asText().equals(
									"com.log.model.command.Search")) {

						Search search = new Search();
						search.setApplicationId(applicationId);
						search.setToken(jsonNode.get("token").asText());
						search.setSize(jsonNode.get("size").getIntValue());
						search.setFrom(jsonNode.get("from").getIntValue());
						
						sendMessage(logIndexer.searchLogs(search));
					} else if (typeNode != null
							&& typeNode.asText().equals(
									"com.log.model.command.Export")) {
						LOGGER.debug("Export logs command : "
								+ jsonNode.toString());
						Export export = new Export();
						export.setApplicationId(applicationId);
						export.setFromDate(new Date(jsonNode.get("fromDate")
								.asLong()));
						export.setToDate(new Date(jsonNode.get("toDate")
								.asLong()));

						sendMessage(logExporter.exportLogs(export));
					} else if (typeNode != null
							&& typeNode.asText().equals(
									"com.log.model.command.GetLogs")) {
						LOGGER.debug("Get logs command : "
								+ jsonNode.toString());
						GetLogs getLogs = new GetLogs();
						getLogs.setApplicationId(applicationId);
						getLogs.setMaxItemNumber(jsonNode.get("maxItemNumber")
								.asInt());
						getLogs.setStartLogId(jsonNode.get("startLogId").asText());
						getLogs.setOffset(jsonNode.get("offset").asInt());
						if(jsonNode.has("ascendingOrder")) {
							getLogs.setAscendingOrder(jsonNode.get("ascendingOrder").asBoolean());
						}else {
							getLogs.setAscendingOrder(true);
						}
						sendMessage(logAccessor.getLogs(getLogs));
					}

				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
