package com.du.tools.sonarqubeservice.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueryUtil {

	@Value("${tools.sonar.rooturl}")
	private String url;

	public Map<String, String> getQueryMap(final String inParam) throws IOException {
		final Map<String, String> queryMap = new HashMap<>();
		final StringBuilder restURL = new StringBuilder();
		restURL.append(url);

		final StringBuilder params = new StringBuilder();
		if (null != inParam && !inParam.isEmpty()) {
			final JSONObject inputJson = new JSONObject(inParam);
			Set<String> paramKeys = inputJson.keySet();
			if (inputJson.has("baseURL")) {
				String baseUrl = inputJson.getString("baseURL");
				restURL.append(baseUrl + "/");
				queryMap.put("baseURL", baseUrl);
				paramKeys.remove("baseURL");
			}
			if (inputJson.has("category")) {
				String category = inputJson.getString("category");
				restURL.append(category);
				queryMap.put("category", category);
				paramKeys.remove("category");
			}

			if (!paramKeys.isEmpty()) {
				params.append("?");
				for (String paramKey : paramKeys) {
					params.append(paramKey + "=" + inputJson.get(paramKey) + "&");
				}
			}
		}
		if (params.length() > 0) {
			params.setLength(params.length() - 1);
			restURL.append(params.toString());
			queryMap.put("restURL", restURL.toString());
		} else {
			queryMap.put("restURL", restURL.toString());
		}
		return queryMap;

	}

}
