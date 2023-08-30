package com.shantanum.inventoryservice.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shantanum.inventoryservice.dto.InventoryResponse;
import com.shantanum.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private static final Logger log = LogManager.getLogger(InventoryController.class);
	
    private final InventoryService inventoryService;

    // http://localhost:8082/api/inventory/iphone-13,iphone13-red

    // http://localhost:8082/api/inventory?skuCode=iphone-13&skuCode=iphone13-red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        log.info("Received inventory check request for skuCode: {}", skuCode);
        //Thread.sleep(10000);
        return inventoryService.isInStock(skuCode);
    }
}

