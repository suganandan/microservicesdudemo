package com.du.tools.rtcservice.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RTCServiceAuthentication {
	private static final Logger LOGGER = LoggerFactory.getLogger(RTCServiceAuthentication.class);

	static String AUTHREQUIRED = "X-com-ibm-team-repository-web-auth-msg";
	@Autowired
	private PropertyUtil propertyUtil;

	public String getresponseData(final String rtcUrl)
			throws NoSuchAlgorithmException, KeyManagementException, IOException {
		String responseString = "Fail";
		HttpClient httpclient = new DefaultHttpClient();

		ClientConnectionManager connManager = httpclient.getConnectionManager();
		SchemeRegistry schemeRegistry = connManager.getSchemeRegistry();
		schemeRegistry.unregister("https");

		TrustManager[] trustAllCerts = { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sc);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme https = new Scheme("https", sf, 443);
		schemeRegistry.register(https);

		String encodedProjectName = URLEncoder.encode("ccm", "UTF-8");

		HttpGet query = new HttpGet(rtcUrl);
		query.addHeader("Accept", "application/xml");
		query.addHeader("OSLC-Core-Version", "2.0");
		org.apache.http.HttpResponse response = null;

		try {
			HttpResponse documentResponse = httpclient.execute(query);

			org.apache.http.Header header = documentResponse.getFirstHeader(AUTHREQUIRED);
			if ((header != null) && ("authrequired".equals(header.getValue()))) {
				documentResponse.getEntity().consumeContent();

				HttpPost formPost = new HttpPost("https://rdm.corp.du.ae/ccm" + "/j_security_check");
				List<NameValuePair> nvps = new ArrayList<>();
				nvps.add(new BasicNameValuePair("j_username", propertyUtil.getPropValues("tools.rtc.uname")));
				nvps.add(new BasicNameValuePair("j_password", propertyUtil.getPropValues("tools.rtc.pwd")));
				formPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

				HttpResponse formResponse = httpclient.execute(formPost);

				header = formResponse.getFirstHeader(AUTHREQUIRED);
				if ((header != null) && ("authfailed".equals(header.getValue()))) {
					throw new InvalidCredentialsException("Authentication failed");
				}
				formResponse.getEntity().consumeContent();

				HttpGet documentGet2 = (HttpGet) query.clone();
				response = httpclient.execute(documentGet2);

			}
			if (null != response) {
				org.apache.http.HttpEntity jsonentity = response.getEntity();
				String xmlString = EntityUtils.toString(jsonentity);
				JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
				responseString = xmlJSONObj.toString();
			}
		} catch (final Exception exception) {

			LOGGER.error(exception.getMessage());
		}

		return responseString;

	}
}
