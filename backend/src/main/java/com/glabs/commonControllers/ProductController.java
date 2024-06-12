package com.glabs.commonControllers;

import com.glabs.commonService.ProductService;
import com.glabs.payload.request.AddProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> addNewProductInCollection(@RequestBody AddProductRequest request){
        return productService.addNewProduct(request);
    }

}
