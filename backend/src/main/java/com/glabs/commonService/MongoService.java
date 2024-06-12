package com.glabs.commonService;

import com.glabs.payload.request.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MongoService {

    private final MongoTemplate mongoTemplate;

    public Set<String> getAllCollectionName(){
        return mongoTemplate.getCollectionNames();
    }
    public List<Document> getAllDocumentInCollection(String collectionName){
        return mongoTemplate.findAll(Document.class, collectionName);
    }

    public long getCountOfDocumentsInCollection(String collectionName) {
        Query query = new Query();
        return mongoTemplate.count(query, collectionName);
    }

    public List<Map> getDocumentsInCollection(String collectionName, Query query){
        return mongoTemplate.find(query, Map.class, collectionName);
    }

    public void insertIntoCollection(Item item, String collectionName) {
        mongoTemplate.insert(item, collectionName);
    }

    public boolean doesCollectionExist(String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }
}
