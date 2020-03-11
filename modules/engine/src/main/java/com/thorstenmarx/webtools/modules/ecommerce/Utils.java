/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce;

import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;

/**
 *
 * @author marx
 */
public abstract class Utils {

	public static int getPageid(final ShardDocument document) {
		Object id = document.document.get(Fields.Page.value());

		return getInt(id);
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
}
