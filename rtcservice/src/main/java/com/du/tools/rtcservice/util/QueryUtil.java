package com.du.tools.rtcservice.util;

import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class QueryUtil {

	public String getQueryString(final String inParam, String restURL) {

		StringBuilder params = new StringBuilder();
		if (null != inParam && !inParam.isEmpty()) {
			final JSONObject inputJson = new JSONObject(inParam);
			Set<String> paramKeys = inputJson.keySet();
			if (!paramKeys.isEmpty()) {
				params.append("?");
				for (String paramKey : paramKeys) {
					params.append(paramKey + "=" + inputJson.get(paramKey) + "&");
				}
			}
		}
		if (params.length() > 0) {
			params.setLength(params.length() - 1);
			restURL = restURL.concat(params.toString());
		}
		return restURL;

	}

}
