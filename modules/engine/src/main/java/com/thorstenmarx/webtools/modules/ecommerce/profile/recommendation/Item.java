/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author marx
 */
public class Item {
	
	public final int id;
	public AtomicInteger count = new AtomicInteger(0);

	public Item(final int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + this.id;
		return hash;
	}

	public int getId() {
		return id;
	}

	public AtomicInteger getCount() {
		return count;
	}
	
	public int getIntCount () {
		return count.get();
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Item other = (Item) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Item{" + "id=" + id + '}';
	}
	
	
	
}
