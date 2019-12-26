package com.tess4j.rest.mongo;

import com.tess4j.rest.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface ImageRepository extends MongoRepository<Image, String> {

     List<Image> findByUserId(String userId);

	void save(Map<String, Image> image);


     
}
