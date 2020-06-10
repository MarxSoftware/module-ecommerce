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
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
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

	

	@Override
	public void handle(final ShardDocument shardDocument) {
		if (Utils.isOrder(shardDocument) && RecentlyViewedProducts.isValid(shardDocument)) {
			// c_cart_items
			final String year_month_day = (shardDocument.document.getString(Fields.YEAR_MONTH_DAY.value()));
			final long timestamp = (shardDocument.document.getLong(Fields._TimeStamp.value()));
			List<Integer> productIDs = Utils.getProductIDs(shardDocument);
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
			LOGGER.debug("is not a valid product");
		}
	}


}
