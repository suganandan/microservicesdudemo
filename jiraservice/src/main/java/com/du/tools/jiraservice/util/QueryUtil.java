package com.du.tools.jiraservice.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueryUtil {

	@Value("${tools.jira.rooturl}")
	private String url;

	public Map<String, String> getQueryMap(final String inParam) throws IOException {
		final Map<String, String> queryMap = new HashMap<>();
		final StringBuilder restURL = new StringBuilder();
		restURL.append(url);

		if (null != inParam && !inParam.isEmpty()) {
			final JSONObject inputJson = new JSONObject(inParam);
			Set<String> paramKeys = inputJson.keySet();
			if (inputJson.has("baseURL")) {
				String baseUrl = inputJson.getString("baseURL");
				restURL.append(baseUrl);
				queryMap.put("baseURL", baseUrl);
				paramKeys.remove("baseURL");
			}
			
			if (inputJson.has("queries")) {
				restURL.append("?"+inputJson.get("queries"));
				paramKeys.remove("queries");
			}
			queryMap.put("restURL", restURL.toString());
			
		}
		
		return queryMap;

	}

}
