/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies.UserStrategy;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies.ItemStrategy;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.Collector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marx
 */
public class RecommendationProfile implements Collector {

	Map<Integer, Order> orders = new HashMap<>();
	
	public List<Item> getItemRecommendations ( final int item_id) {
		return new ItemStrategy(orders).calculate(item_id);
	}

	public List<Item> getUserRecommendations ( final String user_id) {
		return new UserStrategy(orders).calculate(user_id);
	}
	
	@Override
	public void handle(final ShardDocument shardDocument) {
		if (!Utils.isOrder(shardDocument)) {
			return;
		}
		final int order_id = Utils.getInt(shardDocument.document.get(Constants.Fields.ORDER_ID));
		final String user_id = shardDocument.document.getString(Fields.UserId.value());
		List<Integer> productIDs = Utils.getProductIDs(shardDocument);
		
		final Order order;
		if (orders.containsKey(order_id)) {
			order = orders.get(order_id);
		} else {
			order = new Order(order_id, user_id);
			orders.put(order_id, order);
		}
		
		productIDs.forEach((item_id) -> {
			order.addItem(item_id);
		});
	}
	
	
	
}
