/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce;

import com.alibaba.fastjson.JSONArray;
import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public abstract class Utils {

	public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static int getPageid(final ShardDocument document) {
		Object id = document.document.get(Fields.Page.value());

		return getInt(id);
	}

	public static boolean isNullOrEmpty (final String value) {
		return value == null || value.equals("");
	}
	
	public static long getLong(final Object value) {
		if (value instanceof Long) {
			return (long) value;
		}
		return Long.valueOf((String) value);
	}

	public static int getInt(final Object value) {
		if (value instanceof Integer) {
			return (int) value;
		}
		return Integer.valueOf((String) value);
	}

	public static boolean isOrder(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Event.value())) {
			return false;
		}
		final String event = document.document.getString(Fields.Event.value());
		return Events.Order.value().equalsIgnoreCase(event);
	}

	public static boolean isProduct(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Type.value())) {
			return false;
		}
		final String type = document.document.getString(Fields.Type.value());
		return Constants.PostTypes.EasyDigitalDownloads.equalsIgnoreCase(type)
				|| Constants.PostTypes.WOOCommerce.equalsIgnoreCase(type);
	}

	public static boolean isPageView(final ShardDocument document) {
		if (!document.document.containsKey(Fields.Event.value())) {
			return false;
		}
		final String event = document.document.getString(Fields.Event.value());
		return Events.PageView.value().equalsIgnoreCase(event);
	}

	public static List<Integer> getProductIDs(final ShardDocument document) {
		List<Integer> productIds = new ArrayList<>();

		if (document.document.containsKey(Constants.Fields.ORDER_ITEMS)) {
			Object temp = document.document.get(Constants.Fields.ORDER_ITEMS);
			if (temp instanceof JSONArray) {
				JSONArray productArray = (JSONArray) temp;
//				productArray.stream().map(Integer.class::cast).forEach(productIds::add);
				productArray.forEach((obj) -> {
					if (obj instanceof String) {
						productIds.add(Integer.valueOf((String) obj));
					} else if (obj instanceof Integer) {
						productIds.add((int) temp);
					}
				});
			} else if (temp instanceof String) {
				productIds.add(Integer.valueOf((String) temp));
			} else if (temp instanceof Integer) {
				productIds.add((int) temp);
			} else {
				LOGGER.error("unknown type: " + temp);
			}
		}

		return productIds;
	}

	public static double normalize(final double value, final double min, final double max) {
//		return (value - min) / (max - min);
		return (value) / (max);
	}
	public static double normalize(final double value, final double target_value) {
		return (value) / (target_value);
	}
}
