package com.glabs.entities.accessories.services;

import com.glabs.commonService.ProductConfigService;
import com.glabs.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessoriesService {

    private final ProductConfigService productService;

    public ProductResponse getAllAccessories(){
        productService.setCollectionEnd("Accessories");
        return ProductResponse.builder()
                .category("accessories")
                .subCategories(productService.getSubcategories())
                .brands(productService.getBrands())
                .products(productService.getProductMap()).build();
    }
}
