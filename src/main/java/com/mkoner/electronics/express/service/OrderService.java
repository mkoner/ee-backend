package com.mkoner.electronics.express.service;

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
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderParams createOrderParams) throws CustomerNotFoundException, LineItemNotFoundException;

    List<Order> getAllOrders();
    Page<Order> getOrdersFilter(ProductPage productPage, GetOrderFilters getOrderFilters);
    Order getOrderById(Long orderId) throws OrderNotFoundException;
    Order updateOrder(Long orderId, CreateOrderParams createOrderParams) throws OrderNotFoundException, CustomerNotFoundException;
    Void deleteOrder(Long orderId) throws OrderNotFoundException;
    Order cancelOrder(Long orderId) throws OrderNotFoundException;
    Order addLineItem(Long orderId, CreateLineItemParams createLineItemParams) throws OrderNotFoundException, ProductNotFoundException;
    Order removeLineItem(Long orderId, Long lineItemId) throws OrderNotFoundException, LineItemNotFoundException;
}
