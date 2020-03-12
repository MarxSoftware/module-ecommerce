/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.collectors;

import com.alibaba.fastjson.JSONArray;
import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.profile.Collector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class FrequentlyPurchasedProducts implements Collector {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrequentlyPurchasedProducts.class);

	private final Map<Integer, Product> products = new HashMap<>();

	public List<Product> getProducts() {
		final List<Product> resultProducts = new ArrayList<>(this.products.values());
		
		return resultProducts;
	}

	private boolean isOrder(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Event.value())) {
			return false;
		}
		final String event = document.document.getString(Fields.Event.value());
		return Events.Order.value().equalsIgnoreCase(event);
	}

	@Override
	public void handle(final ShardDocument shardDocument) {
		if (isOrder(shardDocument) && RecentlyViewedProducts.isValid(shardDocument)) {
			System.out.println(shardDocument.document.toJSONString());
			// c_cart_items
			final String year_month_day = (shardDocument.document.getString(Fields.YEAR_MONTH_DAY.value()));
			final long timestamp = (shardDocument.document.getLong(Fields._TimeStamp.value()));
			List<Integer> productIDs = getProductIDs(shardDocument);
			productIDs.forEach((prodID) -> {
				final Product temp = new Product(prodID, year_month_day, timestamp);
				if (products.containsKey(prodID)) {
					var product = products.get(prodID);
					product.count = product.count + 1;
					if (temp.year_week.compareTo(product.year_week) >= 0) {
						product.year_week = temp.year_week;
					}
				} else {
					products.put(prodID, temp);
				}
			});
		} else {
			LOGGER.error("is not a valid product");
		}
	}

	public List<Integer> getProductIDs(final ShardDocument document) {
		List<Integer> productIds = new ArrayList<>();

		if (document.document.containsKey("c_order_items")) {
			Object temp = document.document.get("c_order_items");
			if (temp instanceof JSONArray) {
				JSONArray productArray = (JSONArray)temp;
				productArray.stream().map(Integer.class::cast).forEach(productIds::add);
			} else if (temp instanceof String) {
				productIds.add(Integer.valueOf((String)temp));
			} else if (temp instanceof Integer) {
				productIds.add((int)temp);
			} else {
				LOGGER.error("unknown type: " + temp);
			}
		}

		return productIds;
	}
}
