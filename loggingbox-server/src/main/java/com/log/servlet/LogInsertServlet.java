package com.log.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.log.component.LogComponent;
import com.log.model.Level;
import com.log.model.Log;

@Component
public class LogInsertServlet extends HttpServlet {

	private static final long serialVersionUID = 1487728256420276137L;

	@Autowired
	LogComponent logComponent;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Log log = new Log();

		if (req.getParameter("date") != null) {
			log.setDate(new Date(Long.parseLong(req.getParameter("date"))));
		} else {
			log.setDate(new Date());
		}
		log.setData(req.getParameter("data"));
		log.setApplicationId(req.getParameter("applicationId"));
		if (req.getParameter("host") != null) {
			log.setHost(req.getParameter("host"));
		} else {
			log.setHost(req.getRemoteHost());
		}
		if (req.getParameter("type") != null) {
			log.setType(req.getParameter("type"));
		}

		if (req.getParameter("level") != null) {
			try {
				log.setLevel(Level.valueOf(req.getParameter("level")
						.toUpperCase()));
			} catch (Exception ex) {
			}
		}

		logComponent.insertLog(log);

		resp.getWriter().write("OK");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
