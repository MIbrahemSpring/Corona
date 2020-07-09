package com.mohamed.corona.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoronaWW {

	@JsonProperty("TotalConfirmed")
	private String totalConfirmed;

	@JsonProperty("TotalDeaths")
	private String totalDeaths;

	@JsonProperty("TotalRecovered")
	private String totalRecovered;

}
