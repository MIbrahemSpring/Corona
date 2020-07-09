package com.mohamed.corona.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mohamed.corona.Entities.CoronaWWEntity;

@Repository
public interface CoronaWWRepository extends MongoRepository<CoronaWWEntity, String> {

}
