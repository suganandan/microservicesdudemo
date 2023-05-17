package com.du.tools.sonarqubeservice.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.du.tools.sonarqubeservice.model.SQEntity;
@Repository
public interface SQRepositry extends JpaRepository<SQEntity, Integer> {

}
