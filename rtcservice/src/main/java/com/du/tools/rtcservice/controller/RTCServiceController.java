package com.du.tools.rtcservice.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.du.tools.rtcservice.model.ProjectArea;
import com.du.tools.rtcservice.repositry.ProjectAreaRepo;
import com.du.tools.rtcservice.util.PropertyUtil;
import com.du.tools.rtcservice.util.QueryUtil;
import com.du.tools.rtcservice.util.RTCServiceAuthentication;

@RestController
public class RTCServiceController {
	@Autowired
	private PropertyUtil propertyUtil;

	@Autowired
	private QueryUtil queryUtil;

	@Autowired
	private RTCServiceAuthentication rtcServiceAuth;

	private static final Logger LOGGER = LoggerFactory.getLogger(RTCServiceController.class);

	ProjectAreaRepo projectAreaRepo;

	@Autowired
	public RTCServiceController(ProjectAreaRepo projectAreaRepo) {
		this.projectAreaRepo = projectAreaRepo;
	}

	@RequestMapping(value = "/getProjectAreas", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/xml", "application/*" }, produces = { "application/json", "application/xml" })
	public @ResponseBody String getProjectAreas(@RequestBody final String inparam)
			throws NoSuchAlgorithmException, KeyManagementException, IOException

	{
		final String rtcUrl = queryUtil.getQueryString(inparam,
				propertyUtil.getPropValues("tools.rtc.getrepositories"));
		LOGGER.info(rtcUrl);
		final String responseObject = rtcServiceAuth.getresponseData(rtcUrl);
		JSONObject json = new JSONObject(responseObject);
		
		System.out.println("Response    :"+json);
		final List<ProjectArea> projectAreaList = new ArrayList<>();

		JSONObject json1 = (JSONObject) json.get("jp06:project-areas");
		JSONArray jsonArray = (JSONArray) json1.get("jp06:project-area");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject objects = jsonArray.getJSONObject(i);
			ProjectArea projectArea = new ProjectArea();
			projectArea.setProjectAreaDetails(objects.toString());
			projectAreaList.add(projectArea);
		}
		projectAreaRepo.saveAll(projectAreaList);
		return responseObject;

	}

	@RequestMapping(value = "/getWorkItems", method = { RequestMethod.GET, RequestMethod.POST }, consumes = {
			"text/xml", "application/*" }, produces = { "application/json", "application/xml" })
	public @ResponseBody String getWorkItems(@RequestBody final String inparam) throws Exception {

		final String rtcUrl = URLEncoder.encode(
				queryUtil.getQueryString(inparam, propertyUtil.getPropValues("tools.rtc.getWorkItem")), "UTF-8");
		LOGGER.info(rtcUrl);
		final String responseObject = rtcServiceAuth.getresponseData(rtcUrl);
		LOGGER.info("responseObject JSON    :" + responseObject);

		return responseObject;

	}

}
