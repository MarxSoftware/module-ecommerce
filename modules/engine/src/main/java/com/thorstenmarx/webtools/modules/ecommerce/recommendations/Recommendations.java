/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.recommendations;

import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.RecommendationDescription;

/**
 *
 * @author marx
 */
public class Recommendations {
	public static RecommendationDescription SIMILAR_USERS_ALSO_BOUGHT = new RecommendationDescription()
			.setId("similar_users_also_bought")
			.setItemIdField("c_cart_items")
			.setType(RecommendationDescription.Type.User)
			.setUserIdField(Fields.UserId.value())
			.setEvent(Events.Order.value());
}
