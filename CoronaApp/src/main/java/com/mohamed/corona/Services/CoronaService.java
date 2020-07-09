package com.mohamed.corona.Services;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mohamed.corona.DTO.CoronaDto;
import com.mohamed.corona.DTO.CoronaNewestDto;
import com.mohamed.corona.Entities.CoronaEntity;
import com.mohamed.corona.Entities.CoronaWWEntity;
import com.mohamed.corona.Models.CoronaWW;
import com.mohamed.corona.Repositories.CoronaRepository;
import com.mohamed.corona.Repositories.CoronaWWRepository;
import com.mohamed.corona.Utils.CallApi;
import com.mohamed.corona.Utils.ModelMap;
import com.mohamed.corona.Utils.ObjMapper;
import com.mohamed.corona.Utils.Utility;

@Service
public class CoronaService implements ICoronaService {

	@Value("${corona_api_uri}")
	private String coronaApiUri;

	@Autowired
	private CallApi api;

	@Autowired
	private ObjMapper objMapper;

	@Autowired
	private ModelMap modelMapping;

	@Autowired
	private CoronaRepository coronaRepo;

	@Autowired
	private CoronaWWRepository coronaWWRepo;

	@Autowired
	private Utility utility;

	@Override
	@Scheduled(fixedRate = 21600)
	@Async
	public void getCoronaReport() {
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entityToken = new HttpEntity<String>(headers);

		CoronaDto[] coronaResponse = null;
		CoronaWW coronaWWResponse = null;

		String check = api.exchange(this.coronaApiUri + "country/egypt", HttpMethod.GET, entityToken, String.class)
				.getBody();
		String check2 = api.exchange(this.coronaApiUri + "world/total", HttpMethod.GET, entityToken, String.class)
				.getBody();

		if (check != null) {
			try {
				coronaResponse = objMapper.readValue(check, CoronaDto[].class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			// how many records in the DB
			int coronaEntityListSize = coronaRepo.findAll().size();

			// How many records come from the API
			int apiCoronaListSize = coronaResponse.length;

			if (apiCoronaListSize > coronaEntityListSize) {
				for (CoronaDto coronaDto : coronaResponse) {
					LocalDate date = LocalDate.parse(coronaDto.getDate(), inputFormatter);
					String formattedDate = outputFormatter.format(date);
					coronaDto.setDate(formattedDate);

					CoronaEntity coronaEntity = modelMapping.map(coronaDto, CoronaEntity.class);

					// save a new record only
					if (coronaRepo.findByDate(coronaEntity.getDate()) == null)
						coronaRepo.save(coronaEntity);

				}
			}
		}
		if (check2 != null) {
			try {
				coronaWWResponse = objMapper.readValue(check2, CoronaWW.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			CoronaWWEntity coronaWWEntity = modelMapping.map(coronaWWResponse, CoronaWWEntity.class);

			coronaWWRepo.save(coronaWWEntity);

		}

	}

	@Override
	public List<CoronaDto> getCoronaList() {
		List<CoronaDto> CoronaDtoList = new ArrayList<CoronaDto>();
		List<CoronaEntity> storedCoronaList = coronaRepo.findAll();
		for (CoronaEntity ce : storedCoronaList) {
			CoronaDto cdto = new CoronaDto();
			modelMapping.map(ce, cdto);
			CoronaDtoList.add(cdto);
		}

		return CoronaDtoList;
	}

	@Override
	public CoronaDto getLastCoronaReport(String date) {
		CoronaEntity coronaEntity;
		coronaEntity = coronaRepo.findByDate(date);
		if (coronaEntity == null)
			coronaEntity = new CoronaEntity();

		CoronaDto coronaDto = modelMapping.map(coronaEntity, CoronaDto.class);

		// check if some values came Zero from the API
		String newDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -2);
		Date yesterday = calendar.getTime();
		newDate = sdf.format(yesterday);

		int sourceConfirmed = coronaDto.getConfirmed();
		int sourceDeaths = coronaDto.getDeaths();
		int sourceActive = coronaDto.getActive();
		int sourceRecovered = coronaDto.getRecovered();

		int confirmed = 0;
		int active = 0;
		int recovered = 0;
		int deaths = 0;

		if (sourceConfirmed == 0) {
			confirmed = coronaRepo.findByDate(newDate).getConfirmed();
			coronaDto.setSourceConfirmedEmpty(true);
		} else
			confirmed = sourceConfirmed;

		if (sourceDeaths == 0) {
			deaths = coronaRepo.findByDate(newDate).getDeaths();
			coronaDto.setSourceDeathsEmpty(true);
		} else
			deaths = sourceDeaths;

		if (sourceActive == 0) {
			active = coronaRepo.findByDate(newDate).getActive();
			coronaDto.setSourceActiveEmpty(true);
		} else
			active = sourceActive;

		if (sourceRecovered == 0) {
			recovered = coronaRepo.findByDate(newDate).getRecovered();
			coronaDto.setSourceRecoveredEmpty(true);
		} else
			recovered = sourceRecovered;

		coronaDto.setConfirmed(confirmed);
		coronaDto.setDeaths(deaths);
		coronaDto.setActive(active);
		coronaDto.setRecovered(recovered);
		return coronaDto;
	}

	@Override
	public CoronaNewestDto getCoronaNewCases(CoronaDto latestDto) {
//		Confirmed = Deaths + Recovered + Active
		CoronaDto dayBeforeCoronaDto = new CoronaDto();
		List<CoronaDto> dd = getCoronaList();
		
		dayBeforeCoronaDto = dd.get(dd.size() - 2);
		
		if (dd.get(dd.size() - 1).getDeaths() == 0)
			dayBeforeCoronaDto.setDeaths(dd.get(dd.size() - 3).getDeaths());
		if (dd.get(dd.size() - 1).getRecovered() == 0)
			dayBeforeCoronaDto.setRecovered(dd.get(dd.size() - 3).getRecovered());

		CoronaNewestDto cnd = new CoronaNewestDto();
		cnd.setNewCases(latestDto.getConfirmed() - dayBeforeCoronaDto.getConfirmed());
		cnd.setNewDeaths(latestDto.getDeaths() - dayBeforeCoronaDto.getDeaths());
		cnd.setNewRecovered(latestDto.getRecovered() - dayBeforeCoronaDto.getRecovered());

		return cnd;
	}

	@Override
	public List<Integer> getCoronaByMonth(String status) {
		List<Integer> monthlyConfirmedCases = new ArrayList<Integer>();
		List<String> months = new ArrayList<String>();
		months = utility.getFirstDayOfEachMonth(2020);
		List<CoronaEntity> coronaEntity = coronaRepo.findTotalMonthly(months);

		switch (status) {
		case "totalConfirmed":
			coronaEntity.stream().forEach(x -> monthlyConfirmedCases.add(x.getConfirmed()));
			break;

		case "totalDeaths":
			coronaEntity.stream().forEach(x -> monthlyConfirmedCases.add(x.getDeaths()));
			break;

		case "totalRecovered":
			coronaEntity.stream().forEach(x -> monthlyConfirmedCases.add(x.getRecovered()));
			break;

		default:
			break;
		}

		return monthlyConfirmedCases;
	}

	@SuppressWarnings("deprecation")
	@Override
	public CoronaWW getTotalWW() {
		CoronaWW coronaWW = null;
		Optional<CoronaWWEntity> cww = coronaWWRepo.findById("coronaWW");
		if (cww.isPresent()) {
			CoronaWWEntity coronaWWEntity = cww.get();
			coronaWW = modelMapping.map(coronaWWEntity, CoronaWW.class);

			NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
			coronaWW.setTotalConfirmed(nf.format(new Double(coronaWW.getTotalConfirmed())));
			coronaWW.setTotalDeaths(nf.format(new Double(coronaWW.getTotalDeaths())));
			coronaWW.setTotalRecovered(nf.format(new Double(coronaWW.getTotalRecovered())));

		}
		return coronaWW;
	}

}
