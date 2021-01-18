/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce;

/**
 *
 * @author marx
 */
public abstract class Constants {
	
	public static class PostTypes {
		public static final String WOOCommerce = "product";
		public static final String EasyDigitalDownloads = "download";
	}
	
	public static class Events {
		public static final String ECOMMERCE_ORDER = "ecommerce_order";
		public static final String ECOMMERCE_CART_ITEM_ADD = "ecommerce_cart_item_add";
		public static final String ECOMMERCE_CART_ITEM_REMOVE = "ecommerce_cart_item_remove";
	}
	public static class Fields {
		public static final String ORDER_ID = "c_order_id";
		public static final String ORDER_ITEMS = "c_order_items";
		public static final String ORDER_TOTAL = "c_order_total";
		public static final String ORDER_COUPONS_COUNT = "c_order_coupons_count";
		public static final String ORDER_COUPONS_USED = "c_order_coupons_used";
		public static final String CART_ID = "c_cart_id";
		public static final String CART_ITEMS = "c_cart_items";
		
		public static final String ECOM_CATEGORIES_PATH = "ecom_categories_path";
		public static final String ECOM_CATEGORIES = "c_ecom_categories";
	}
}
