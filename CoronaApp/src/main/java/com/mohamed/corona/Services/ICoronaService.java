package com.mohamed.corona.Services;

import java.util.List;

import com.mohamed.corona.DTO.CoronaDto;
import com.mohamed.corona.DTO.CoronaNewestDto;
import com.mohamed.corona.Models.CoronaWW;

public interface ICoronaService {

	void getCoronaReport();
	
	List<CoronaDto> getCoronaList();
	
	CoronaDto getLastCoronaReport(String date);
	
	CoronaWW getTotalWW();
	
	CoronaNewestDto getCoronaNewCases(CoronaDto latestDto);
	
	List<Integer> getCoronaByMonth(String status);
}
