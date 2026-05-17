package com.Ifrah.javaproject;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface yellowrepo extends MongoRepository<yellow, String> {
    // Additional queries (if needed) can be defined here
}

