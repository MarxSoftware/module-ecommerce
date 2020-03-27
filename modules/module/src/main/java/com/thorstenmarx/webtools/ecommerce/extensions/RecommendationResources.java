/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.ecommerce.extensions;

import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Events;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.ProfileGenerator;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.FrequentlyPurchasedProducts;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Item;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.RecommendationProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
	
	private final CacheLayer cacheLayer;
	
	public static final String bought_together = "bought_together";
	public static final String similar_users = "similar_users";
	
	protected RecommendationResources (final AnalyticsDB analyticsDB, final CacheLayer cacheLayer) {
		this.analyticsDB = analyticsDB;
		this.cacheLayer = cacheLayer;
	}
	
	private String key(final String prefix, final String site, final String id) {
		return String.format("ecommerce-%s-%s", prefix, site);
	}
	
	@GET
	@Path("/bought_together")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO recommendation_bought_together(@QueryParam("site") final String site, @QueryParam("product") final Integer product_id) {
		
		final ProfileDTO dto = new ProfileDTO();
		if (Utils.isNullOrEmpty(site) || product_id == null) {
			dto.put("error", true);
			dto.put("message", "missing required parameters");
			return dto;
		}
		
		if (cacheLayer.exists(key(bought_together, site, String.valueOf(product_id)))) {
			Optional<ProfileDTO> cacheValue = cacheLayer.get(key(bought_together, site, String.valueOf(product_id)), ProfileDTO.class);
			if (cacheValue.isPresent()) {
				return cacheValue.get();
			}
		}
		

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
		
		cacheLayer.add(key(bought_together, site, String.valueOf(product_id)), dto, 10, TimeUnit.SECONDS);
		
		return dto;
	}
	
	
	@GET
	@Path("/similar_users")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO similar_users(@QueryParam("site") final String site, @QueryParam("user") final String user) {
		
		final ProfileDTO dto = new ProfileDTO();
		if (Utils.isNullOrEmpty(site) || Utils.isNullOrEmpty(user)) {
			dto.put("error", true);
			dto.put("message", "missing required parameters");
			return dto;
		}
		
		if (cacheLayer.exists(key(similar_users, site, user))) {
			Optional<ProfileDTO> cacheValue = cacheLayer.get(key(similar_users, site, user), ProfileDTO.class);
			if (cacheValue.isPresent()) {
				return cacheValue.get();
			}
		}
		
		

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
		
		cacheLayer.add(key(similar_users, site, user), dto, 10, TimeUnit.SECONDS);
		return dto;
	}
	
	private static Product itemToProduct (final Item item) {
		var product = new Product(item.id, "", 0);
		product.count = item.getIntCount();
		return product;
	}
}
