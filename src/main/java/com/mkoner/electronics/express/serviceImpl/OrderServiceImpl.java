package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.Customer;
import com.mkoner.electronics.express.entity.LineItem;
import com.mkoner.electronics.express.entity.Order;
import com.mkoner.electronics.express.enumeration.OrderStatus;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.LineItemNotFoundException;
import com.mkoner.electronics.express.exceptions.OrderNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateLineItemParams;
import com.mkoner.electronics.express.params.CreateOrderParams;
import com.mkoner.electronics.express.params.GetOrderFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.repository.LineItemRepository;
import com.mkoner.electronics.express.repository.OrderCriteriaRepository;
import com.mkoner.electronics.express.repository.OrderRepository;
import com.mkoner.electronics.express.service.CustomerService;
import com.mkoner.electronics.express.service.LineItemService;
import com.mkoner.electronics.express.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderCriteriaRepository orderCriteriaRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private LineItemService lineItemService;
    @Autowired
    private LineItemRepository lineItemRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Order createOrder(CreateOrderParams createOrderParams) throws CustomerNotFoundException, LineItemNotFoundException {
        Order order= new Order();
        Customer customer = customerService.getCustomerById(createOrderParams.getCustomerId());
        List<LineItem> lineItems = new ArrayList<>();
        for(Long lineItemId:createOrderParams.getLineItemIds()){
            lineItems.add(lineItemService.getLineItemById(lineItemId));
        }
        order.setCustomer(customer);
        order.setLineItems(lineItems);
        order.setShippingAddress(createOrderParams.getAddress());
        order.setOrderStatus(OrderStatus.NEW);
        order.setOrderAmount(order.calculateOrderAmount());
        order.calculateOrderAmount();
        return orderRepository.save(order);
    }

    @Override
    @Cacheable(value = "all-orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Cacheable(value = "orders")
    public Page<Order> getOrdersFilter(ProductPage productPage, GetOrderFilters getOrderFilters) {
        if(Objects.isNull(productPage.getSortBy()) || productPage.getSortBy().isBlank()){
            productPage.setSortBy("orderId");
        }
        return orderCriteriaRepository.findAllFiltered(productPage, getOrderFilters);
    }

    @Override
    public Order getOrderById(Long orderId) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isEmpty())
            throw new OrderNotFoundException(ExceptionMessages.ORDER_NOT_FOUND);
        return order.get();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Order updateOrder(Long orderId, CreateOrderParams createOrderParams) throws OrderNotFoundException, CustomerNotFoundException {

        Order orderToUpdate = getOrderById(orderId);
        Customer customer = null;

        if(Objects.nonNull(createOrderParams.getCustomerId())) {
            customer = customerService.getCustomerById(createOrderParams.getCustomerId());
            orderToUpdate.setCustomer(customer);
        }
        if(Objects.nonNull(createOrderParams.getAddress()))
            orderToUpdate.setShippingAddress(createOrderParams.getAddress());
        if(Objects.nonNull(createOrderParams.getOrderStatus()))
            orderToUpdate.setOrderStatus(OrderStatus.valueOf(createOrderParams.getOrderStatus()));
        orderToUpdate.setModifiedAt(new Date());
        orderToUpdate.calculateOrderAmount();
        return orderRepository.save(orderToUpdate);
    }

    @Override
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Void deleteOrder(Long orderId) throws OrderNotFoundException {
        getOrderById(orderId);
        orderRepository.deleteById(orderId);
        return null;
    }

    @Override
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Order cancelOrder(Long orderId) throws OrderNotFoundException {
        Order order = getOrderById(orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setOrderCancellationDate(new Date());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Order addLineItem(Long orderId, CreateLineItemParams createLineItemParams) throws OrderNotFoundException, ProductNotFoundException {
        Order order = getOrderById(orderId);
        LineItem lineItem = lineItemService.createLineItem(createLineItemParams);
        order.addLineItem(lineItem);
        order.setOrderAmount(order.calculateOrderAmount());
        order.setModifiedAt(new Date());
        order.calculateOrderAmount();
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"all-orders", "orders"}, allEntries = true)
    public Order removeLineItem(Long orderId, Long lineItemId) throws OrderNotFoundException, LineItemNotFoundException {
        Order order = getOrderById(orderId);
        LineItem lineItem = lineItemService.getLineItemById(lineItemId);
        order.removeLineItem(lineItem);
        order.setOrderAmount(order.calculateOrderAmount());
        order.setModifiedAt(new Date());
        lineItemService.deleteLineItem(lineItemId);
        return orderRepository.save(order);
    }
}
