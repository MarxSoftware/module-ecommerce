/**
 * WebTools-eCommerce
 * Copyright (C) 2016  Thorsten Marx (kontakt@thorstenmarx.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thorstenmarx.webtools.modules.ecommerce.recommendations;

/*-
 * #%L
 * recommendations-engine
 * %%
 * Copyright (C) 2018 - 2019 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.Interval;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.RecommendationDescription;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.TimeRange;

import com.thorstenmarx.webtools.test.MockAnalyticsDB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



/**
 *
 * @author marx
 */
public class EngineTest {
	
	MockAnalyticsDB analyticsDb = new MockAnalyticsDB();
	
	private Engine engine;
	
	@BeforeClass
	private void setup () {
		
		Multimap<String, String> data = ArrayListMultimap.create();
		user(data, "u1", "p1", "p2", "p3", "p4");
		user(data, "u2", "p1", "p4");
		user(data, "u3", "p1", "p2", "p4");
		user(data, "u4", "p2", "p3");
		user(data, "u5", "p1", "p3", "p4");
	
		filldb(data, analyticsDb);
		
		RecommendationDescription userDescription = new RecommendationDescription()
				.setId("user")
				.setType(RecommendationDescription.Type.User)
				.setName("user also bought")
				.setItemIdField("c_order_items")
				.setUserIdField("user");
		TimeRange timeRange = new TimeRange().setCount(2).setInterval(Interval.DAY);
		userDescription.setTimeRange(timeRange);
		RecommendationDescription itemDescription = new RecommendationDescription()
				.setId("item")
				.setType(RecommendationDescription.Type.Item)
				.setName("often bought tougether")
				.setItemIdField("c_order_items")
				.setUserIdField("user");
		itemDescription.setTimeRange(timeRange);
		
		engine = new Engine(analyticsDb);
		engine.addRecommendationDescription(itemDescription);
		engine.addRecommendationDescription(userDescription);
	}
	

	

	@Test()
	public void testUserRecommendation() {
		List<String> recommendations = engine.recommendation("user").recommend("u4", 3);
		Assertions.assertThat(recommendations).contains("p1", "p4").hasSize(2);
	}
	@Test()
	public void testItemRecommendation() {
		List<String> recommendations = engine.recommendation("item").recommend("p1", 3);
		Assertions.assertThat(recommendations).contains("p2", "p3", "p4").hasSize(3);
	}

	
	@Test(dependsOnMethods = {"testItemRecommendation", "testUserRecommendation"})
	public void testClose() {
		engine.close();
	}
	
	
	private void user (Multimap<String, String> map, final String user, final String... items) {
		map.putAll(user, Arrays.asList(items));
	}

	public static void filldb (final Multimap<String, String> data, final AnalyticsDB db) {
		for (final String userid : data.keySet()) {
			Map<String, Object> source = new HashMap<>();
			source.put("user", userid);
			
			List<String> array = new ArrayList<>();
			for (final String itemid : data.get(userid)) {
				array.add(itemid);
			}
			source.put("c_order_items", array);
			
			Map<String, Map<String, Object>> event = new HashMap<>();
			event.put("data", source);
			db.track(event);
		}
	}
	
	public static List<ShardDocument> createDocuments(final Multimap<String, String> data) {
		List<ShardDocument> documents = new ArrayList<>();

		for (final String userid : data.keySet()) {
			JSONObject source = new JSONObject();
			source.put("user", userid);
			
			JSONArray array = new JSONArray();
			for (final String itemid : data.get(userid)) {
				array.add(itemid);
			}
			source.put("c_order_items", array);
			

			documents.add(new ShardDocument("shard1", source));
		}

		return documents;
	}
}
