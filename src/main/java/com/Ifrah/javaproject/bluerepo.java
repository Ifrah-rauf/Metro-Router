package com.Ifrah.javaproject;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface bluerepo extends MongoRepository<blue, String> {
    // Additional queries (if needed) can be defined here
}

