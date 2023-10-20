package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.LineItem;
import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.LineItemNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateLineItemParams;
import com.mkoner.electronics.express.repository.LineItemRepository;
import com.mkoner.electronics.express.service.LineItemService;
import com.mkoner.electronics.express.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LineItemServiceImpl implements LineItemService {

    @Autowired
    private LineItemRepository lineItemRepository;
    @Autowired
    private ProductService productService;

    @Override
    public LineItem createLineItem(CreateLineItemParams createLineItemParams) throws ProductNotFoundException {
        LineItem lineItem = new LineItem();
        Product product = productService.getProductById(createLineItemParams.getProductId());
        lineItem.setProduct(product);
        lineItem.setLineItemQuantity(createLineItemParams.getLineItemQuantity());
        lineItem.setLineItemPrice(lineItem.getLineItemQuantity() * product.getProductPrice());
        return lineItemRepository.save(lineItem);
    }

    @Override
    public List<LineItem> getAllLineItems() {
        return lineItemRepository.findAll();
    }

    @Override
    public LineItem getLineItemById(Long lineItemId) throws LineItemNotFoundException {
        Optional<LineItem> lineItem = lineItemRepository.findById(lineItemId);
        if(lineItem.isEmpty())
            throw new LineItemNotFoundException(ExceptionMessages.LINE_ITEM_NOT_FOUND);
        return lineItem.get();
    }

    @Override
    public LineItem updateLineItem(Long lineItemId, LineItem lineItem) throws LineItemNotFoundException {
        LineItem lineItemToUpdate = getLineItemById(lineItemId);
        lineItemToUpdate.setLineItemQuantity(lineItem.getLineItemQuantity());
        lineItemToUpdate.setLineItemPrice(lineItem.getLineItemQuantity() * lineItemToUpdate.getProduct().getProductPrice());
        lineItemToUpdate.setModifiedAt(new Date());
        return lineItemRepository.save(lineItemToUpdate);
    }

    @Override
    public Void deleteLineItem(Long lineItemId) throws LineItemNotFoundException {
        getLineItemById(lineItemId);
        lineItemRepository.deleteById(lineItemId);
        return null;
    }
}
