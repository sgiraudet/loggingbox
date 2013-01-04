package com.loggingbox.servlet;
//package com.log.servlet;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.TimeUnit;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.catalina.websocket.MessageInbound;
//import org.apache.catalina.websocket.StreamInbound;
//import org.apache.catalina.websocket.WebSocketServlet;
//import org.apache.catalina.websocket.WsOutbound;
//import org.codehaus.jackson.JsonGenerationException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.log.core.ApplicationRegistration;
//import com.log.core.RegistrationContainer;
//import com.log.model.Application;
//import com.log.model.ApplicationObject;
//import com.log.model.KpiDefinition;
//import com.log.model.Log;
//import com.log.storage.ApplicationAccessor;
//import com.log.storage.LogAccessor;
//
//@Component
//public class LogGetServlet extends WebSocketServlet {
//
//	private static final long serialVersionUID = 5518097059496417942L;
//
//	private final ObjectMapper objectMapper = new ObjectMapper();
//
//	@Autowired
//	RegistrationContainer registrationContainer;
//
//	@Autowired
//	LogAccessor logAccessor;
//	@Autowired
//	ApplicationAccessor applicationAccessor;
//
//	private class LogWebWebSocket extends MessageInbound {
//
//		private ApplicationRegistration applicationRegistration;
//		private final String applicationId;
//
//		private WsOutbound outbound;
//
//		public LogWebWebSocket(final String applicationId) {
//			this.applicationId = applicationId;
//		}
//
//		@Override
//		public void onOpen(WsOutbound outbound) {
//			this.outbound = outbound;
//			//this.connection.setMaxIdleTime((int) TimeUnit.HOURS.toMillis(10));
//			
//
//			Application application = new Application();
//			application.setId(applicationId);
//			this.applicationRegistration = new ApplicationRegistration(
//					application);
//
//			registrationContainer.register(applicationRegistration);
//
//			for (Application otherAppli : applicationAccessor.getApplications()) {
//				sendMessage(otherAppli);
//			}
//
//			List<KpiDefinition> kpis = applicationAccessor
//					.getKpiDefinitions(applicationId);
//			for (KpiDefinition kpiDefinition : kpis) {
//				sendMessage(kpiDefinition);
//			}
//			List<Log> oldLogs = logAccessor.getLogs(application.getId(), null,
//					1000);
//			for (int i = oldLogs.size() - 1; i >= 0; i--) {
//				Log log = oldLogs.get(i);
//				sendMessage(log);
//
//			}
//			Timer timer = new Timer();
//			timer.schedule(new TimerTask() {
//
//				@Override
//				public void run() {
//					getMessages();
//
//				}
//			}, 0);
//		}
//
//		void getMessages() {
//			while (outbound != null && applicationRegistration != null) {
//				ApplicationObject object = applicationRegistration
//						.getNextObject();
//				if (object != null) {
//					sendMessage(object);
//				}
//			}
//		}
//
//		@Override
//		protected void onClose(int status) {
//			this.outbound = null;
//			if (applicationRegistration != null) {
//				registrationContainer.unregister(applicationRegistration);
//				applicationRegistration = null;
//			}
//		}
//	
//
//		private void sendMessage(ApplicationObject object) {
//			try {
//				outbound.writeTextMessage(CharBuffer.wrap(objectMapper
//						.writeValueAsString(object).toCharArray()));
//			} catch (JsonGenerationException e) {
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//			} catch (IOException e) {
//				System.err.println(e);
//				outbound = null;
//			}
//		}
//
//		@Override
//		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
//
//		}
//
//		@Override
//		protected void onTextMessage(CharBuffer arg0) throws IOException {
//
//		}
//
//		@Override
//		public int getReadTimeout() {
//			return (int) TimeUnit.HOURS.toMillis(10);
//		}
//
//	}
//
//	@Override
//	protected StreamInbound createWebSocketInbound(String arg0,
//			HttpServletRequest arg1) {
//		return new LogWebWebSocket(arg1.getParameter("appId"));
//	}
//
//}
