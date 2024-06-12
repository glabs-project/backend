package com.glabs.commonControllers;

import com.glabs.commonService.ProductService;
import com.glabs.payload.request.AddProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Работа с продуктами", description = "Позволяет добавить продукты в выбранную подкатегорию")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "Добавление продукта",
            description = "category - глобальная категория, например Guitar, Case. subCategory - подкатегория, например classical, electro." +
                    "В итоге название колекции должно совпадать с названием колекции в БД"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "нет данной колекции"),
            @ApiResponse(responseCode = "400", description = "Отсутсвует одно из полей продукта или запроса"),
            @ApiResponse(responseCode = "200", description = "OK - продукт добавлен")
    })
    @PostMapping
    public ResponseEntity<?> addNewProductInCollection(@RequestBody AddProductRequest request){
        return productService.addNewProduct(request);
    }

}
