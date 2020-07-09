package com.mohamed.corona.Models;

import lombok.Data;

@Data
public class CoronaResponse {

	private String country;
	private String countryCode;
	private String province;
	private String city;
	private String cityCode;
	private String lat;
	private String lon;
	private int confirmed;
	private int deaths;
	private int recovered;
	private int active;
	private String date;

}
