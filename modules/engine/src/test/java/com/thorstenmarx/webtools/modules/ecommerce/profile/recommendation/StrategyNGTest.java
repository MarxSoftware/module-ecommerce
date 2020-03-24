/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class StrategyNGTest {
	
	
	private Order createOrder (final int id, final int...items) {
		Order order = new Order(id);
		for (int item_id : items) {
			order.addItem(new Item(item_id));
		}
		return order;
	}

	@Test
	public void test_strategy() {
		Map<Integer, Order> orders = new HashMap<>();
		orders.put(1, createOrder(1, new int [] {1, 2, 3}));
		orders.put(2, createOrder(2, new int [] {1, 2, 3}));
		orders.put(3, createOrder(3, new int [] {2, 3}));
		orders.put(4, createOrder(4, new int [] {1, 3}));
	
		Strategy stragety = new Strategy(orders);
		
		List<Item> calculate = stragety.calculate(1);
		
		System.out.println(calculate);
		Assertions.assertThat(calculate).containsExactly(new Item(3), new Item(2));
		
		Assertions.assertThat(calculate.get(0))
				.hasFieldOrPropertyWithValue("id", 3)
				.hasFieldOrPropertyWithValue("intCount", 3);
		Assertions.assertThat(calculate.get(1))
				.hasFieldOrPropertyWithValue("id", 2)
				.hasFieldOrPropertyWithValue("intCount", 2);
	}
	
}
