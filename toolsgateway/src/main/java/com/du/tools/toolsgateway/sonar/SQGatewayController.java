package com.du.tools.toolsgateway.sonar;

import java.sql.Timestamp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@EnableScheduling
public class SQGatewayController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SQGatewayController.class);
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RetryListener retryListener;
	@Value("${my.app.maxAttempts}")
	private int retryCnt;
	private boolean flag ;

	/*
	 * Circuit Breaker Pattern: If there are failures in your microservices
	 * ecosystem, then you need to fail fast by opening the circuit. This ensures
	 * that no additional calls are made to the failing service, once the circuit
	 * breaker is open. So we return an exception immediately. This pattern also
	 * monitor the system for failures and once things are back to normal, the
	 * circuit is closed to allow normal functionality.
	 */
	@Autowired
	private CircuitBreakerFactory<?, ?> circuitBreakerFactory;

	@RequestMapping(value = "/getSonarQubeData", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })
	public @ResponseBody String getSonarQubeData(@RequestBody final String inparam) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

		String responseMessage = null;
		final JSONObject inputJson = new JSONObject(inparam);

		try {
			final String response = circuitBreaker.run(() -> restTemplate
					.postForObject("http://sq-service/getSQDataset", inputJson.toString(), String.class),
					throwable -> getFailService());
			responseMessage = response;
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
			responseMessage = exception.getMessage();
		}
		return responseMessage;
	}

	private String getFailService() {
		LOGGER.info("SQ-SERVICE is not available   :" + new Timestamp(System.currentTimeMillis()));
		return "SQ-SERVICE is not available   :" + new Timestamp(System.currentTimeMillis());
	}

	@RequestMapping(value = "/getSQDataset", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })

	@Retryable(maxAttemptsExpression = "#{${my.app.maxAttempts}}", value = IllegalArgumentException.class, backoff = @Backoff(delayExpression = "#{${my.app.backOffDelay}}"),listeners = {"retryListener"})
	public @ResponseBody String getSQDataset(@RequestBody final String inparam) {
		
		String responseMessage = null;
		final JSONObject inputJson = new JSONObject(inparam);

		try {
			final String response = restTemplate.postForObject("http://sq-service/getSQDataset", inputJson.toString(),
					String.class);
			responseMessage = response;
			flag=false;
		} catch (final IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage() + " : " + new Timestamp(System.currentTimeMillis()));
			System.out.println("count      :"+retryListener.getRetryCount());
			if(retryCnt==retryListener.getRetryCount()+1) {
				System.out.println(" persist the data into DB");
				flag=true;
			}
			System.out.println(" Falg    :"+flag);
			throw illegalArgumentException;
		} catch (final Exception exception) {
			
			LOGGER.error(exception.getMessage() + " : " + new Timestamp(System.currentTimeMillis()));
		}
		final JSONObject outJson = new JSONObject(responseMessage);
		if(outJson.has("status")) {
			if(outJson.get("status").equals(500)) {
				flag=true;
				System.out.println("===================>"+inputJson.toString());
			}
		}
		return responseMessage;
	}
	
	@Scheduled(cron = "${my.cron.expression}")
	public void scheduleJobs() {
		if(flag) {
			System.out.println("inside scheduler");
		}

	}

}
