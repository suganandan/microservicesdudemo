package com.du.tools.jiraservice.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.du.tools.jiraservice.model.JiraEntity;

@Repository
public interface JiraRepositry extends JpaRepository<JiraEntity, Integer> {

}
