package com.du.tools.toolsscheduler;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@RestController
public class ToolsschedulerApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(ToolsschedulerApplication.class);
	@Value("${tools.sonar.gateway}")
	private String sonarGateway;

	public static void main(String[] args) {
		SpringApplication.run(ToolsschedulerApplication.class, args);
	}

	@RequestMapping(value = "/startjob", method = { RequestMethod.GET, RequestMethod.POST })
	public void execute() {
		scheduleJobs();
	}

	@Scheduled(cron = "${my.cron.expression}")
	public void scheduleJobs() {
		final JSONObject inputJson = new JSONObject();
		inputJson.put("baseURL", "measures");
		inputJson.put("category", "component");
		inputJson.put("component", "com.du.pgw:pgw");
		inputJson.put("additionalFields", "periods,metrics");
		inputJson.put("metricKeys", "ncloc,complexity,violations");
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.postForObject(sonarGateway, inputJson.toString(), String.class);
		LOGGER.info(response);

	}

}
