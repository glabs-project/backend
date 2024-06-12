package com.glabs.commonControllers;

import com.glabs.commonService.ProductService;
import com.glabs.entities.accessories.services.AccessoriesService;
import com.glabs.entities.amplifiers.services.AmplifierService;
import com.glabs.entities.cases.services.CaseService;
import com.glabs.entities.guitars.services.GuitarService;
import com.glabs.entities.pedals.services.PedalService;
import com.glabs.entities.strings.services.StringService;
import com.glabs.entities.switching.services.SwitchingService;
import com.glabs.payload.request.AddProductRequest;
import com.glabs.payload.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Работа с продуктами", description = "Позволяет добавить продукты в выбранную подкатегорию")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/")
public class ProductController {

    private final ProductService productService;
    private final AccessoriesService accessoriesService;
    private final AmplifierService amplifierService;
    private final CaseService caseService;
    private final GuitarService guitarService;
    private final PedalService pedalService;
    private final StringService stringService;
    private final SwitchingService switchingService;

    @Operation(
            summary = "Добавление продукта",
            description = "category - глобальная категория, например Guitar, Case. subCategory - подкатегория, например classical, electro." +
                    "В итоге название колекции должно совпадать с названием колекции в БД"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "нет данной колекции при вставке"),
            @ApiResponse(responseCode = "400", description = "Отсутсвует одно из полей продукта или запроса"),
            @ApiResponse(responseCode = "200", description = "OK - продукт добавлен")
    })
    @PostMapping
    public ResponseEntity<?> addNewProductInCollection(@RequestBody AddProductRequest request){
        return productService.addNewProduct(request);
    }

    @GetMapping("accessories")
    private ProductResponse getAllAccessories(){
        return accessoriesService.getAllAccessories();
    }

    @GetMapping("amps")
    private ProductResponse getAllAmplifier(){
        return amplifierService.getAllAmp();
    }

    @GetMapping("cases")
    private ProductResponse getAllCases() { return  caseService.getAllCase(); }

    @GetMapping("guitars")
    private ProductResponse getAllGuitars() { return guitarService.getAllGuitar(); }

    @GetMapping("pedals")
    private ProductResponse getAllPedals() { return pedalService.getAllPedals(); }

    @GetMapping("strings")
    private ProductResponse getAllStrings() { return stringService.getAllStrings(); }

    @GetMapping("switching")
    private ProductResponse getAllSwitching() { return switchingService.getAllSwitching(); }
}
