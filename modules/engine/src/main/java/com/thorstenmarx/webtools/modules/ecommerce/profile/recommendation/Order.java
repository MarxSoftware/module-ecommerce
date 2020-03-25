/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author marx
 */
public class Order {

	public final int id;
	public final String user_id;
	public final List<Item> items = new ArrayList<>();

	public Order(final int id, final String user_id) {
		this.id = id;
		this.user_id = user_id;
	}

	public void addItem(final int id) {
		Optional<Item> findAny = items.stream().filter((item) -> item.id == id).findAny();
		if (findAny.isPresent()) {
			findAny.get().count.incrementAndGet();
		} else {
			Item item = new Item(id);
			item.count.incrementAndGet();
			items.add(item);
		}
	}

	public boolean hasItem(final int id) {
		Optional<Item> findAny = items.stream().filter((item) -> item.id == id).findAny();
		return findAny.isPresent();
	}

}
