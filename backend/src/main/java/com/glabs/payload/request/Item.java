package com.glabs.payload.request;

import java.util.ArrayList;

public record Item(String name, String price, String type, ArrayList<String> characteristics,
                   ArrayList<String> images) {
}
