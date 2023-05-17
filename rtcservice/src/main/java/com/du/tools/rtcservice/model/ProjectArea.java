package com.du.tools.rtcservice.model;

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
@Table(name = "project_area")
@Data
public class ProjectArea {

	@EqualsAndHashCode.Exclude
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int id;

	@EqualsAndHashCode.Include
	@NotEmpty(message = "Project Area Details is required")
	@Column(columnDefinition = "json")

	private String projectAreaDetails;

}
