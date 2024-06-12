package com.glabs.entities.switching.services;

import com.glabs.commonService.ProductConfigService;
import com.glabs.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SwitchingService {

    private final ProductConfigService productService;

    public ProductResponse getAllSwitching(){
        productService.setCollectionEnd("Switching");
        return ProductResponse.builder()
                .category("switching")
                .subCategories(productService.getSubcategories())
                .brands(productService.getBrands())
                .products(productService.getProductMap()).build();
    }
}
