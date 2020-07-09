package com.mohamed.corona.Entities;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "coronaWW")
public class CoronaWWEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id = "coronaWW";
	private String totalConfirmed;
	private String totalDeaths;
	private String totalRecovered;
}
