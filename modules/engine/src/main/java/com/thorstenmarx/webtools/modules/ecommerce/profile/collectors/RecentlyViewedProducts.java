/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.collectors;

import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import com.thorstenmarx.webtools.modules.ecommerce.SortOrder;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.Collector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class RecentlyViewedProducts implements Collector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RecentlyViewedProducts.class);

	
	private final Map<Integer, Product> products = new HashMap<>();

	public List<Product> getProducts() {
		final List<Product> resultProducts = new ArrayList<>(this.products.values());
		return resultProducts;
	}

	private boolean isProduct(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Type.value())) {
			return false;
		}
		final String type = document.document.getString(Fields.Type.value());
		return Constants.PostTypes.EasyDigitalDownloads.equalsIgnoreCase(type) 
				|| Constants.PostTypes.WOOCommerce.equalsIgnoreCase(type);
	}
	private boolean isPageView(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Event.value())) {
			return false;
		}
		final String event = document.document.getString(Fields.Event.value());
		return Events.PageView.value().equalsIgnoreCase(event);
	}

	@Override
	public void handle(final ShardDocument shardDocument) {
		if (isProduct(shardDocument) && isPageView(shardDocument)) {
			// c_cart_items
			int page = Utils.getPageid(shardDocument);
			final String year_month_day = (shardDocument.document.getString(Fields.YEAR_MONTH_DAY.value()));
			final Product temp = new Product(page, year_month_day);
			if (products.containsKey(page)) {
				var product = products.get(page);
				product.count = product.count + 1;
				if (temp.year_week.compareTo(product.year_week) >= 0) {
					product.year_week = temp.year_week;
				}
			} else {
				products.put(page, temp);
			}
		} else {
			LOGGER.error("is not a product");
		}
	}
}
