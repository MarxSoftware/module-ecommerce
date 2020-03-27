/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.ecommerce.extensions;

import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.modules.ecommerce.profile.ProfileGenerator;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.FrequentlyPurchasedProducts;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Item;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.RecommendationProfile;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author marx
 */
public class RecommendationResources {
	
	private final AnalyticsDB analyticsDB;
	
	protected RecommendationResources (final AnalyticsDB analyticsDB) {
		this.analyticsDB = analyticsDB;
	}
	
	@GET
	@Path("/bought_together")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO recommendation_bought_together(@QueryParam("site") final String site, @QueryParam("product") final int product_id) {
		System.out.println("get the recommendation for " + site);
		final ProfileDTO dto = new ProfileDTO();

		var recommendationProfile = new RecommendationProfile();
		ProfileGenerator userProfile = ProfileGenerator.builder(analyticsDB, site, ProfileGenerator.Type.SHOP)
				.event(Events.Order.value())
				.addCollector(recommendationProfile)
				.build();
		System.out.println("before generate");
		userProfile.generate();
		System.out.println("after generate");
		
		List<Item> itemRecommendations = recommendationProfile.getItemRecommendations(product_id);
		
		List<Product> recommendations = new ArrayList<>();
		itemRecommendations.stream().map(RecommendationResources::itemToProduct).forEach(recommendations::add);
		
		if (recommendations.size() > 10) {
			recommendations = recommendations.subList(0, 10);
		}
		dto.put("bought_together", recommendations);
		return dto;
	}
	
	
	@GET
	@Path("/similar_users")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO similar_users(@QueryParam("site") final String site, @QueryParam("user") final String user) {
		System.out.println("get the recommendation for " + site);
		final ProfileDTO dto = new ProfileDTO();

		var recommendationProfile = new RecommendationProfile();
		ProfileGenerator userProfile = ProfileGenerator.builder(analyticsDB, site, ProfileGenerator.Type.SHOP)
				.event(Events.Order.value())
				.addCollector(recommendationProfile)
				.build();
		System.out.println("before generate");
		userProfile.generate();
		System.out.println("after generate");
		
		List<Item> itemRecommendations = recommendationProfile.getUserRecommendations(user);
		
		List<Product> recommendations = new ArrayList<>();
		itemRecommendations.stream().map(RecommendationResources::itemToProduct).forEach(recommendations::add);
		
		if (recommendations.size() > 10) {
			recommendations = recommendations.subList(0, 10);
		}
		dto.put("similar_users", recommendations);
		return dto;
	}
	
	private static Product itemToProduct (final Item item) {
		var product = new Product(item.id, "", 0);
		product.count = item.getIntCount();
		return product;
	}
}
