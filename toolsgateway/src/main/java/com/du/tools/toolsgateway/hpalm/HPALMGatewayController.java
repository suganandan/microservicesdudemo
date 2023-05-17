package com.du.tools.toolsgateway.hpalm;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HPALMGatewayController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HPALMGatewayController.class);
	@Autowired
	RestTemplate restTemplate;

	@RequestMapping(value = "/getHpalmData", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })

	public @ResponseBody String getHpalmData(@RequestBody final String inparam) {
		String responseMessage = null;
		JSONObject inputJson = new JSONObject(inparam);
		if (null != inparam && !inparam.isEmpty()) {
			inputJson = new JSONObject(inparam);
		}
		try {
			String response = restTemplate.postForObject("http://hpalm-service/getHpalmData", inputJson.toString(),
					String.class);
			responseMessage = response;
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
			responseMessage = exception.getMessage();
		}
		return responseMessage;
	}

}
