package com.mohamed.corona.DTO;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoronaDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Country")
	private String country;

	@JsonProperty("CountryCode")
	private String countryCode;

	@JsonProperty("Confirmed")
	private int confirmed;

	@JsonProperty("Deaths")
	private int deaths;

	@JsonProperty("Recovered")
	private int recovered;

	@JsonProperty("Active")
	private int active;

	@JsonProperty("Date")
	private String date;
	
	@JsonIgnore
	private boolean isSourceConfirmedEmpty;
	
	@JsonIgnore
	private boolean isSourceDeathsEmpty;
	
	@JsonIgnore
	private boolean isSourceActiveEmpty;
	
	@JsonIgnore
	private boolean isSourceRecoveredEmpty;

}
