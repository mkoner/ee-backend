package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.LineItem;
import com.mkoner.electronics.express.exceptions.LineItemNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateLineItemParams;

import java.util.List;

public interface LineItemService {
    LineItem createLineItem(CreateLineItemParams createLineItemParams) throws ProductNotFoundException;
    List<LineItem> getAllLineItems();
    LineItem getLineItemById(Long lineItemId) throws LineItemNotFoundException;
    LineItem updateLineItem(Long lineItemId, LineItem lineItem) throws LineItemNotFoundException;
    Void deleteLineItem(Long lineItemId) throws LineItemNotFoundException;
}
