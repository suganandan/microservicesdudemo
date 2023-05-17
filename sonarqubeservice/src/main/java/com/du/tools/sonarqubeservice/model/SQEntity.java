
package com.du.tools.sonarqubeservice.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sq_data")
@Data

public class SQEntity {

	@EqualsAndHashCode.Exclude
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@EqualsAndHashCode.Include
	@NotEmpty(message = "Metric Details  is required")
	@Column(columnDefinition = "json")
	// @Column(columnDefinition = "CLOB")
	private String responsedata;

	@EqualsAndHashCode.Include
	private String serviceType;

	@EqualsAndHashCode.Include
	private String category;

	@EqualsAndHashCode.Exclude
	@Basic
	private Timestamp timestamp;

}
