/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies;

import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Item;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Order;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Strategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author marx
 */
public class ItemStrategy implements Strategy<Integer> {

	final Map<Integer, Order> orders;
	
	public ItemStrategy (final Map<Integer, Order> orders) {
		this.orders = orders;
	}
	
	@Override
	public List<Item> calculate(final Integer item_id) {
		
		if (item_id == null) {
			return Collections.EMPTY_LIST;
		}
		
		final Map<Integer, Item> items = new HashMap<>();
		
		// found all orders containing the target item
		// only orders with more than one item
		List<Order> ordersWithItem = orders.values().stream().filter((order) -> {
			return order.hasItem(item_id);
		}).filter((order) -> order.items.size() > 1).collect(Collectors.toList());

		// count the also bought items
		ordersWithItem.forEach((order) -> {
			order.items.stream().filter((item) -> (item.id != item_id)).map((item) -> {
				final Item item_add;
				if (items.containsKey(item.id)) {
					item_add = items.get(item.id);
				} else {
					item_add = new Item(item.id);
					items.put(item.id, item_add);
				}
				return item_add;
			}).forEachOrdered((item_add) -> {
				item_add.count.incrementAndGet();
			});
		});
		
		Comparator<Item> reverseOrderComparator = Collections.reverseOrder(Comparator.comparingInt((final Item value) -> value.getCount().get()));
		
		List<Item> productItems = items.values().stream().filter((item) -> item.count.get() > 1)
				.sorted(reverseOrderComparator)
				.collect(Collectors.toList());
		
		return productItems;
	}
}
