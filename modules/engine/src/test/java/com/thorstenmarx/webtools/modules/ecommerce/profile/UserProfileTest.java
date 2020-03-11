/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile;


import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import com.thorstenmarx.webtools.modules.ecommerce.SortOrder;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.RecentlyViewedProducts;
import com.thorstenmarx.webtools.test.MockAnalyticsDB;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class UserProfileTest {
	
	MockAnalyticsDB analyticsDB;
	
	@BeforeClass
	public void setup () {
		analyticsDB = new MockAnalyticsDB();
		analyticsDB.track(event("user1", 1, "2020-05-01"));
		analyticsDB.track(event("user1", 1, "2020-05-02"));
		analyticsDB.track(event("user1", 2, "2020-06-01"));
	}
	
	private Map<String, Map<String, Object>> event (final String userid, final int page, final String year_week) {
		Map<String, Map<String, Object>> event = new HashMap<>();
		event.put("meta", new HashMap<>());
		
		Map<String, Object> data = new HashMap<>();
		data.put(Fields.Type.value(), Constants.PostTypes.WOOCommerce);
		data.put(Fields._TimeStamp.value(), System.currentTimeMillis());
		data.put(Fields.YEAR_MONTH_DAY.value(), year_week);
		data.put(Fields.Event.value(), Events.PageView.value());
		data.put(Fields.Page.value(), page);
		data.put(Fields.UserId.value(), userid);
		event.put("data", data);
		
		return event;
	}

	@Test
	public void test_product_order_by_date() {
		RecentlyViewedProducts recentlyViewed = new RecentlyViewedProducts();
		ProfileGenerator profile = ProfileGenerator.builder(analyticsDB, "user1", ProfileGenerator.Type.USER).addCollector(recentlyViewed).build();
		
		profile.generate();
		
		List<Product> products = recentlyViewed.getProducts();
		products.sort(new Product.By_Date(SortOrder.Ascending));
		Assertions.assertThat(products).hasSize(2);
		Assertions.assertThat(products.get(0).id).isEqualTo(1);
		Assertions.assertThat(products.get(1).id).isEqualTo(2);
		
		products = recentlyViewed.getProducts();
		products.sort(new Product.By_Date(SortOrder.Descending));
		Assertions.assertThat(products).hasSize(2);
		Assertions.assertThat(products.get(0).id).isEqualTo(2);
		Assertions.assertThat(products.get(1).id).isEqualTo(1);
	}

	@Test
	public void test_product_order_by_count() {
		RecentlyViewedProducts recentlyViewed = new RecentlyViewedProducts();
		ProfileGenerator profile = ProfileGenerator.builder(analyticsDB, "user1", ProfileGenerator.Type.USER).addCollector(recentlyViewed).build();
		
		profile.generate();
		
		List<Product> products = recentlyViewed.getProducts();
		products.sort(new Product.By_Count(SortOrder.Descending));
		Assertions.assertThat(products).hasSize(2);
		Assertions.assertThat(products.get(0).id).isEqualTo(1);
		Assertions.assertThat(products.get(1).id).isEqualTo(2);
		
		products = recentlyViewed.getProducts();
		products.sort(new Product.By_Count(SortOrder.Ascending));
		Assertions.assertThat(products).hasSize(2);
		Assertions.assertThat(products.get(0).id).isEqualTo(2);
		Assertions.assertThat(products.get(1).id).isEqualTo(1);
	}	
}
