package com.glabs.payload.request;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document
public class AddProductRequest {
    private String category;
    private String subCategory;
    private ArrayList<Item> items;
}
