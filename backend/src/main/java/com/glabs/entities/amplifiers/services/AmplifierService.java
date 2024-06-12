package com.glabs.entities.amplifiers.services;

import com.glabs.commonService.ProductConfigService;
import com.glabs.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AmplifierService {

    private final ProductConfigService productService;

    public ProductResponse getAllAmp() {
        productService.setCollectionEnd("Amp");
        return ProductResponse.builder()
                .category("amp")
                .subCategories(productService.getSubcategories())
                .brands(productService.getBrands())
                .products(productService.getProductMap()).build();
    }

}
