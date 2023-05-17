package com.du.tools.rtcservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.du.tools.rtcservice.repositry.ProjectAreaRepo;

@Service
public class ProjectAreaService {

	ProjectAreaRepo projectAreaRepo;

	@Autowired
	public ProjectAreaService(ProjectAreaRepo projectAreaRepo) {
		this.projectAreaRepo = projectAreaRepo;
	}
}
