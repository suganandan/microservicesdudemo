package com.du.tools.jiraservice.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.du.tools.jiraservice.model.JiraEntity;
import com.du.tools.jiraservice.repositry.JiraRepositry;
import com.du.tools.jiraservice.util.BasicAuthRestTemplate;
import com.du.tools.jiraservice.util.QueryUtil;

@RestController
public class JiraServiceController {

	@Autowired
	private QueryUtil queryUtil;

	private BasicAuthRestTemplate restTemplate;

	@Autowired
	JiraRepositry jiraRepositry;

	@Value("${jira.uname}")
	private String uname;

	@Value("${jira.pwd}")
	private String pwd;

	@Value("${server.port}")
	private String port;

	private static final Logger LOGGER = LoggerFactory.getLogger(JiraServiceController.class);

	@PostConstruct
	public void postConstruct() {
		this.restTemplate = new BasicAuthRestTemplate(uname, pwd);

	}

	@RequestMapping(value = "/getJiraData", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })
	public @ResponseBody String getJiraData(@RequestBody final String inparam) throws IOException {
		String response = null;
		try {
			Map<String, String> queryMap = queryUtil.getQueryMap(inparam);
			String restURL = queryMap.get("restURL");

			ResponseEntity<String> result = restTemplate.getForEntity(restURL, String.class);

			response = result.getBody();
			List<JiraEntity> fetchlist = jiraRepositry.findAll();
			JiraEntity jiraEntity = new JiraEntity();
			jiraEntity.setResponsedata(response);
			jiraEntity.setServiceType(queryMap.get("baseURL"));
			jiraEntity.setCategory(queryMap.get("category"));
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			jiraEntity.setTimestamp(ts);
			if (!fetchlist.contains(jiraEntity)) {
				jiraRepositry.save(jiraEntity);
			}

			LOGGER.info(result.getBody());
		} catch (final IOException ioException) {
			LOGGER.error(ioException.getMessage());
			response = ioException.getMessage();
		} catch (final IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage());
			response = illegalArgumentException.getMessage();
		}

		return response;
	}

	@RequestMapping(value = "/jiraQueryExecution", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })
	public @ResponseBody String jiraQueryExecution(@RequestBody final String inparam) throws IOException {
		String response = null;
		try {
			final JSONObject tempJson = new JSONObject(inparam);
			Map<String, String> queryMap = queryUtil.getQueryMap(inparam);
			String restURL = queryMap.get("restURL");
			JSONArray strArray = null;
			if (tempJson.has("queries")) {
				strArray = tempJson.getJSONArray("queries");
			}
			ResponseEntity<String> result = restTemplate.postForEntity(restURL, strArray, String.class);
			response = result.getBody();
			List<JiraEntity> fetchlist = jiraRepositry.findAll();
			JiraEntity jiraEntity = new JiraEntity();
			jiraEntity.setResponsedata(response);
			jiraEntity.setServiceType(queryMap.get("baseURL"));
			jiraEntity.setCategory(queryMap.get("category"));
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			jiraEntity.setTimestamp(ts);
			if (!fetchlist.contains(jiraEntity)) {
				jiraRepositry.save(jiraEntity);
			}

			LOGGER.info(result.getBody());
		} catch (final IOException ioException) {
			LOGGER.error(ioException.getMessage());
			response = ioException.getMessage();
		} catch (final IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage());
			response = illegalArgumentException.getMessage();
		}

		return response;
	}
}
