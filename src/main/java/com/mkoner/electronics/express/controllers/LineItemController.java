package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.entity.LineItem;
import com.mkoner.electronics.express.exceptions.LineItemNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateLineItemParams;
import com.mkoner.electronics.express.service.LineItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/line-items")
public class LineItemController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private LineItemService lineItemService;

    @PostMapping("")
    public ResponseEntity<LineItem> createLineItem(@RequestBody CreateLineItemParams createLineItemParams) throws ProductNotFoundException {
        return new ResponseEntity<>(lineItemService.createLineItem(createLineItemParams), HttpStatus.CREATED);
    }

    @GetMapping("")
    public List<LineItem> getAllLineItem(){
        return lineItemService.getAllLineItems();
    }

    @GetMapping("/id/{id}")
    public LineItem getLineItem(@PathVariable("id") Long id) throws LineItemNotFoundException {
        return lineItemService.getLineItemById(id);
    }

    @PutMapping("/{id}")
    public LineItem updateLineItem(@PathVariable("id") Long id, @RequestBody LineItem lineItem) throws LineItemNotFoundException {
        LOGGER.info("updateLineItem", lineItem, id);
        return lineItemService.updateLineItem(id, lineItem);
    }

    @DeleteMapping("/{id}")
    public Void deleteLineItem(@PathVariable("id") Long id) throws LineItemNotFoundException {
        return lineItemService.deleteLineItem(id);
    }
}
