package com.du.tools.sonarqubeservice.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.du.tools.sonarqubeservice.repositry.SQRepositry;
import com.du.tools.sonarqubeservice.util.BasicAuthRestTemplate;
import com.du.tools.sonarqubeservice.util.QueryUtil;

@RestController
public class SonarQubeServiceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SonarQubeServiceController.class);

	@Autowired
	private QueryUtil queryUtil;

	private BasicAuthRestTemplate restTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	SQRepositry sqRepositry;

	@Value("${service.uname}")
	private String uname;

	@Value("${service.pwd}")
	private String pwd;

	@Value("${server.port}")
	private String port;

	@PostConstruct
	public void postConstruct() {
		this.restTemplate = new BasicAuthRestTemplate(uname, pwd);

	}

	@RequestMapping(value = "/getSQDataset", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/plain", "application/*" })
	public @ResponseBody String getSQDataset(@RequestBody final String inparam) throws IOException {
		String response = null;
		try {
			Map<String, String> queryMap = queryUtil.getQueryMap(inparam);
			String restURL = queryMap.get("restURL");
			File file = new File("C:\\Users\\a786218\\Desktop\\bscs.csv");
			
			for (int i = 1; i < 21; i++) {
				String restURL1 = restURL.concat("&p=" + i);
				ResponseEntity<String> result = restTemplate.getForEntity(restURL, String.class);
				response = result.getBody();

				JSONObject output = new JSONObject(response);
				JSONArray docs = output.getJSONArray("issues");

				String csv = CDL.toString(docs);

				FileUtils.writeStringToFile(file, csv);
				System.out.println("Data has been Sucessfully Writeen to " + file);
			}
			/*
			 * List<SQEntity> fetchlist = sqRepositry.findAll(); SQEntity sqEntity = new
			 * SQEntity(); sqEntity.setResponsedata(response);
			 * sqEntity.setServiceType(queryMap.get("baseURL"));
			 * sqEntity.setCategory(queryMap.get("category")); Timestamp ts = new
			 * Timestamp(System.currentTimeMillis()); sqEntity.setTimestamp(ts); if
			 * (!fetchlist.contains(sqEntity)) { sqRepositry.save(sqEntity); }
			 * 
			 * LOGGER.info(result.getBody());
			 */
		} catch (final IOException ioException) {
			LOGGER.error(ioException.getMessage());
			response = ioException.getMessage();
		} catch (final IllegalArgumentException illegalArgumentException) {
			LOGGER.error(illegalArgumentException.getMessage());
			response = illegalArgumentException.getMessage();
		}

		return response;
	}

	public JSONObject getQueryResult(final JSONObject input) {
		final JSONObject output = new JSONObject();
		try {
			if (input.has("query")) {
				final String query = input.getString("query");
				if (input.has("params")) {
					JSONObject params = input.getJSONObject("params");
					final MapSqlParameterSource paramMap = new MapSqlParameterSource();
					for (String key : params.keySet()) {
						paramMap.addValue(key, params.get(key));
					}
					final Map<String, Object> queryResult = namedParameterJdbcTemplate.queryForMap(query, paramMap);
					for (String key : queryResult.keySet()) {
						output.put(key.toLowerCase(), queryResult.get(key));
					}
				}
			}
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
		}
		return output;
	}

	public JSONObject getQueryInsert(final JSONObject input) {
		boolean result = false;
		final JSONObject output = new JSONObject();
		try {
			if (input.has("query")) {
				final String query = input.getString("query");
				if (input.has("params")) {
					JSONObject params = input.getJSONObject("params");
					final MapSqlParameterSource paramMap = new MapSqlParameterSource();
					for (String key : params.keySet()) {
						paramMap.addValue(key, params.get(key));
					}
					int count = namedParameterJdbcTemplate.update(query, paramMap);
					if (count > 0) {
						result = true;
					}
				}
			}
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage());
		}
		output.put("result", result);
		return output;
	}

}
