/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies.ItemStrategy;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies.UserStrategy;
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
	
	
	private Order createOrder (final String user_id, final int id, final int...items) {
		Order order = new Order(id, user_id);
		for (int item_id : items) {
			order.addItem(item_id);
		}
		return order;
	}

	@Test
	public void test_item_strategy() {
		Map<Integer, Order> orders = new HashMap<>();
		orders.put(1, createOrder("u1", 1, new int [] {1, 2, 3}));
		orders.put(2, createOrder("u1", 2, new int [] {1, 2, 3}));
		orders.put(3, createOrder("u2", 3, new int [] {2, 3}));
		orders.put(4, createOrder("u3", 4, new int [] {1, 3}));
	
		ItemStrategy stragety = new ItemStrategy(orders);
		
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
	
	@Test
	public void test_user_strategy() {
		Map<Integer, Order> orders = new HashMap<>();
		orders.put(1, createOrder("u1", 1, new int [] {1, 2}));
		orders.put(2, createOrder("u1", 2, new int [] {1, 3}));
		orders.put(3, createOrder("u2", 3, new int [] {2, 3, 4, 10, 11}));
		orders.put(4, createOrder("u3", 4, new int [] {1, 3}));
		orders.put(5, createOrder("u4", 5, new int [] {1, 2, 3, 4, 5}));
		orders.put(6, createOrder("u5", 6, new int [] {22}));
		orders.put(7, createOrder("u6", 7, new int [] {2, 3, 4, 5}));
	
		UserStrategy stragety = new UserStrategy(orders);
		
		List<Item> calculate = stragety.calculate("u2");
		Assertions.assertThat(calculate).containsExactly(new Item(1), new Item(5));
		
		Assertions.assertThat(calculate.get(0))
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("intCount", 3);
		Assertions.assertThat(calculate.get(1))
				.hasFieldOrPropertyWithValue("id", 5)
				.hasFieldOrPropertyWithValue("intCount", 2);
	}
	
	@Test
	public void test_user_strategy_unknown() {
		Map<Integer, Order> orders = new HashMap<>();
		orders.put(1, createOrder("u1", 1, new int [] {1, 2}));
		orders.put(2, createOrder("u1", 2, new int [] {1, 3}));
	
		UserStrategy stragety = new UserStrategy(orders);
		
		List<Item> calculate = stragety.calculate("u2");
		Assertions.assertThat(calculate).isEmpty();
		
	}
}
