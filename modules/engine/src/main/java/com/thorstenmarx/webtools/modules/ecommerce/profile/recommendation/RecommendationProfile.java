/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.Collector;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author marx
 */
public class RecommendationProfile implements Collector {

	Map<Integer, Order> orders = new HashMap<>();
	
	public List<Product> products = new ArrayList<>();
	
	public List<Product> getRecommendations ( final int item_id, final Strategy strategy) {
		
		return Collections.EMPTY_LIST;
	}
	
	@Override
	public void handle(final ShardDocument shardDocument) {
		if (!Utils.isOrder(shardDocument)) {
			return;
		}
		int order_id = Utils.getInt(shardDocument.document.get(Constants.Fields.ORDER_ID));
		List<Integer> productIDs = Utils.getProductIDs(shardDocument);
		
		final Order order;
		if (orders.containsKey(order_id)) {
			order = orders.get(order_id);
		} else {
			order = new Order(order_id);
			orders.put(order_id, order);
		}
		
		productIDs.forEach((item_id) -> {
			if (!order.hasItem(item_id)) {
				order.addItem(new Item(item_id));
			}
		});
	}
	
	
	
}
