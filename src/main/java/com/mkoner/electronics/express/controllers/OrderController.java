package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.entity.LineItem;
import com.mkoner.electronics.express.entity.Order;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.LineItemNotFoundException;
import com.mkoner.electronics.express.exceptions.OrderNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateLineItemParams;
import com.mkoner.electronics.express.params.CreateOrderParams;
import com.mkoner.electronics.express.params.GetOrderFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @PostMapping("")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderParams createOrderParams) throws LineItemNotFoundException,
            CustomerNotFoundException {
        return new ResponseEntity<>(orderService.createOrder(createOrderParams), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("")
    public Page<Order> getOrdersFilter(ProductPage productPage, GetOrderFilters getOrderFilters){
        return orderService.getOrdersFilter(productPage,getOrderFilters);
    }

    @GetMapping("/id/{id}")
    public Order getOrderById(@PathVariable("id") Long orderId) throws OrderNotFoundException {
        return orderService.getOrderById(orderId);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable("id") Long orderId, @RequestBody CreateOrderParams createOrderParams) throws
            OrderNotFoundException, CustomerNotFoundException {
        return orderService.updateOrder(orderId, createOrderParams);
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable("id") Long orderId) throws OrderNotFoundException {
        LOGGER.info("Cancel" + orderId);
        return orderService.cancelOrder(orderId);
    }

    @PutMapping("/{orderId}/add-line-item")
    public Order addLineItem(@PathVariable("orderId") Long orderId, @RequestBody CreateLineItemParams createLineItemParams) throws OrderNotFoundException, LineItemNotFoundException, ProductNotFoundException {
        return orderService.addLineItem(orderId, createLineItemParams);
    }

    @DeleteMapping("/{orderId}/line-item/{lineItemId}")
    public Order removeLineItem(@PathVariable("orderId") Long orderId, @PathVariable("lineItemId") Long lineItemId) throws OrderNotFoundException, LineItemNotFoundException {
        return orderService.removeLineItem(orderId,lineItemId);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") Long orderId) throws OrderNotFoundException {
       orderService.deleteOrder(orderId);
    }
}
