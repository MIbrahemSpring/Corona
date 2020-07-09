package com.mohamed.corona.Entities;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "corona")
public class CoronaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String country;
	private String countryCode;
	private int confirmed;
	private int deaths;
	private int recovered;
	private int active;
	private String date;
}
