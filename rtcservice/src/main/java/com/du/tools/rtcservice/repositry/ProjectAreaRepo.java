package com.du.tools.rtcservice.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.du.tools.rtcservice.model.ProjectArea;
@Repository
public interface ProjectAreaRepo extends JpaRepository<ProjectArea, Integer> {

}
