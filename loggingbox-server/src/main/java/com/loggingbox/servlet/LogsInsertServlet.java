package com.loggingbox.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtil;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.log.component.LogComponent;
import com.log.model.Level;
import com.log.model.Log;

@Component
public class LogsInsertServlet extends HttpServlet {


//	private static final Logger LOGGER = Logger.getLogger(LogsInsertServlet.class);
	
	private static final long serialVersionUID = 1487728256420276137L;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	LogComponent logComponent;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String logsString;

			String contentEncoding = req.getHeader("Content-Encoding");
			if(contentEncoding != null && contentEncoding.equals("gzip")) {
				GZIPInputStream is = new GZIPInputStream(req.getInputStream());
				logsString = IOUtil.toString(is,  "UTF-8");
				is.close();
			}else {
				logsString = IOUtil.toString(req.getInputStream());
			}

			JsonNode jsonNode = objectMapper.readTree(logsString);
			List<Log> logsToInsert = new ArrayList<Log>();
			if (jsonNode.isArray()) {
				for (int i = 0; i < jsonNode.size(); i++) {
					JsonNode logNode = jsonNode.get(i);

					Log log = new Log();

					if (logNode.get("date") != null) {
						log.setDate(new Date(logNode.get("date").asLong()));
					} else {
						log.setDate(new Date());
					}
					log.setData(logNode.get("data").asText());
					log.setApplicationId(logNode.get("applicationId").asText());

					if (logNode.get("host") != null) {
						log.setHost(logNode.get("host").asText());
					} else {
						log.setHost(req.getRemoteHost());
					}
					if (logNode.get("type") != null) {
						log.setType(logNode.get("type").asText());
					}

					if (logNode.get("level") != null) {
						try {
							log.setLevel(Level.valueOf(logNode.get("level")
									.asText().toUpperCase()));
						} catch (Exception ex) {
						}
					}
					logsToInsert.add(log);

				}

			}
			logComponent.insertLog(logsToInsert);
			resp.getWriter().write("OK");
		} catch (Exception ex) {
			ex.printStackTrace();
			resp.getWriter().write("KO");
		}
	}
}
