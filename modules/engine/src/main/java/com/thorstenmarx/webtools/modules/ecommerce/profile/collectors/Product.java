/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.collectors;

import com.thorstenmarx.webtools.modules.ecommerce.SortOrder;
import java.util.Comparator;

/**
 *
 * @author marx
 */
public class Product {

	public final int id;
	public String year_week;
	public int count;
	
	public final long timestamp;

	public Product(final int id, final String year_week, final long timestamp) {
		this.id = id;
		this.year_week = year_week;
		this.count = 1;
		this.timestamp = timestamp;
	}	

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + this.id;
		return hash;
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
		final Product other = (Product) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	public static class By_Date implements Comparator<Product> {

		
		final SortOrder sortOder;
		public By_Date (final SortOrder sortOder) {
			this.sortOder = sortOder;
		}
		
		@Override
		public int compare(Product o1, Product o2) {
			if (SortOrder.Ascending.equals(sortOder)) {
				return Long.compare(o1.timestamp, o2.timestamp);
//				return o1.year_week.compareTo(o2.year_week);
			} else {
				return Long.compare(o2.timestamp, o1.timestamp);
//				return o2.year_week.compareTo(o1.year_week);
			}
			
		}
		
	}
	public static class By_Count implements Comparator<Product> {

		
		final SortOrder sortOder;
		public By_Count (final SortOrder sortOder) {
			this.sortOder = sortOder;
		}
		
		@Override
		public int compare(Product o1, Product o2) {
			if (SortOrder.Ascending.equals(sortOder)) {
				if (o1.count > o2.count) {
					return 1;
				} else if (o2.count > o1.count) {
					return -1;
				} else {
					return 0;
				}
			} else {
				if (o2.count > o1.count) {
					return 1;
				} else if (o1.count > o2.count) {
					return -1;
				} else {
					return 0;
				}
			}
			
		}
		
	}
}
