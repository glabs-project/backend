package com.glabs.entities.guitars.services;

import com.glabs.commonService.ProductConfigService;
import com.glabs.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuitarService {
    private final ProductConfigService productService;
    public ProductResponse getAllGuitar() {
        productService.setCollectionEnd("Guitar");
        return ProductResponse.builder()
                .category("guitar")
                .subCategories(productService.getSubcategories())
                .brands(productService.getBrands())
                .products(productService.getProductMap()).build();
    }

}
