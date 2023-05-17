package com.du.tools.rtcservice.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class PropertyUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

	public   String getPropValues(final String query) throws IOException {
		String result = null;
		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			String propFileName = "api.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

			result = prop.getProperty(query);

		} catch (final Exception exception) {
			LOGGER.error("Exception: " + exception);
		} finally {
			if (null != inputStream) {
				inputStream.close();
			}
		}
		return result;
	}
}
