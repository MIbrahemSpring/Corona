package com.mohamed.corona.Controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mohamed.corona.DTO.CoronaDto;
import com.mohamed.corona.DTO.CoronaNewestDto;
import com.mohamed.corona.Services.ICoronaService;
import com.mohamed.corona.Utils.Enums;
import com.mohamed.corona.Utils.Utility;

@Controller
public class HomeController {

	@Autowired
	private ICoronaService _coronaService;

	@Autowired
	private Utility utility;

	@GetMapping
	public String getHome(Model model) {
		CoronaDto latestConfirmed = _coronaService.getLastCoronaReport(utility.getDayBeforeFormated(new Date()));
		CoronaNewestDto newestDto = _coronaService.getCoronaNewCases(latestConfirmed);
		Map<String, Object> attrs = new HashMap<>();
		attrs.put("coronaTotal", latestConfirmed);
		attrs.put("coronaNewest", newestDto);
		attrs.put("totalConfirmed", _coronaService.getCoronaByMonth(Enums.Status.totalConfirmed.toString()));
		attrs.put("totalDeaths", _coronaService.getCoronaByMonth(Enums.Status.totalDeaths.toString()));
		attrs.put("totalRecovered", _coronaService.getCoronaByMonth(Enums.Status.totalRecovered.toString()));
		attrs.put("totalNewCases", _coronaService.getCoronaByMonth(Enums.Status.totalNew.toString()));
		attrs.put("totalCasesWW", _coronaService.getTotalWW());
		attrs.put("deathsNotUpdated", latestConfirmed.isSourceDeathsEmpty());
		attrs.put("activeNotUpdated", latestConfirmed.isSourceActiveEmpty());
		attrs.put("recoveredNotUpdated", latestConfirmed.isSourceRecoveredEmpty());
		attrs.put("confirmedNotUpdated", latestConfirmed.isSourceConfirmedEmpty());
		model.addAllAttributes(attrs);
		return "home";
	}
}
