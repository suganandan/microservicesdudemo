package com.du.tools.toolsgateway.jira;

import java.sql.Timestamp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
public class JiraGatewayController {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraGatewayController.class);
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping(value = "/getJiraData", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })
	@Retryable(maxAttempts = 9, value = IllegalArgumentException.class, backoff = @Backoff(delay = 10000))
	public @ResponseBody String getJiraData(@RequestBody final String inparam) {

		String responseMessage = null;
		final JSONObject inputJson = new JSONObject(inparam);

		try {
			final String response = restTemplate.postForObject("http://jira-service/getJiraData",
					inputJson.toString(), String.class);
			responseMessage = response;
		} catch (final IllegalArgumentException exception) {
			LOGGER.error(exception.getMessage() + " : " + new Timestamp(System.currentTimeMillis()));
			throw exception;
		}
		return responseMessage;
	}
}
