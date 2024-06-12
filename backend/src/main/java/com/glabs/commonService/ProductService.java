package com.glabs.commonService;

import com.glabs.payload.request.AddProductRequest;
import com.glabs.payload.request.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MongoService mongoService;

    public ResponseEntity<?> addNewProduct(AddProductRequest request) {
        if(!mongoService.doesCollectionExist(request.getSubCategory() + request.getCategory())){
            return ResponseEntity.notFound().build();
        }

        Assert.notNull(request.getCategory(), "Category must not be null");
        Assert.notNull(request.getSubCategory(), "SubCategory must not be null");
        ArrayList<Item> items = request.getItems();
        Assert.notNull(items, "Items must not be null");

        for (Item item : items) {
            validateItem(item);
            mongoService.insertIntoCollection(item, request.getSubCategory() + request.getCategory());
        }

        return ResponseEntity.ok().build();
    }

    private void validateItem(Item item) {
        Assert.notNull(item.name(), "Item name must not be null");
        Assert.notNull(item.price(), "Item price must not be null");
        Assert.notNull(item.type(), "Item type must not be null");
        Assert.notNull(item.characteristics(), "Item characteristics must not be null");
        Assert.notNull(item.images(), "Item images must not be null");
    }

}
