package com.mohamed.corona.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mohamed.corona.Entities.CoronaEntity;

@Repository
public interface CoronaRepository extends MongoRepository<CoronaEntity, String> {

	@Query("{'date': ?0}")
	CoronaEntity findByDate(String Date);
	
	@Query("{date: { $in : [?0]}}")
	List<CoronaEntity> findTotalMonthly(List<String> months);
}