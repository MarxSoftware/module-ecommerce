/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.ecommerce.extensions.rest;

import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.api.extensions.SecureRestResourceExtension;
import com.thorstenmarx.webtools.ecommerce.extensions.ProfileDTO;
import com.thorstenmarx.webtools.modules.ecommerce.SortOrder;
import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.ProfileGenerator;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.FrequentlyPurchasedProducts;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.RecentlyViewedProducts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author marx
 */
@Extension(SecureRestResourceExtension.class)
public class ECommerceRestExtension extends SecureRestResourceExtension {

	@Inject
	private AnalyticsDB analyticsDB;

	@Inject
	private CacheLayer cacheLayer;

	private static final String user_profile = "userprofile";
	private static final String shop_profile = "shopprpfile";

	@Override
	public void init() {
	}

	private String key(final String prefix, final String site) {
		return String.format("ecommerce-%s-%s", prefix, site);
	}

	@GET
	@Path("/shopprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO shopprofile(@QueryParam("site") final String site, @QueryParam("category") @DefaultValue("NONE") String category, @QueryParam("count") @DefaultValue("10") int count) {

		final ProfileDTO dto = new ProfileDTO();
		if (Utils.isNullOrEmpty(site)) {
			dto.put("error", true);
			dto.put("message", "missing required parameters");
			return dto;
		}
		
		if (cacheLayer.exists(key(shop_profile, site))) {
			Optional<ProfileDTO> cacheValue = cacheLayer.get(key(shop_profile, site), ProfileDTO.class);
			if (cacheValue.isPresent()) {
				return cacheValue.get();
			}
		}

		var frequentlyPurchasedProducts = new FrequentlyPurchasedProducts();
		final ProfileGenerator.Builder profileGeneratorBuilder = ProfileGenerator.builder(analyticsDB, site, ProfileGenerator.Type.SHOP)
				.addCollector(frequentlyPurchasedProducts);
		if (!"NONE".equals("category")) {
			profileGeneratorBuilder.category(category);
		}
		ProfileGenerator profileGenerator = profileGeneratorBuilder
				.build();

		profileGenerator.generate();

		List<Product> products = frequentlyPurchasedProducts.getProducts();
		if (products.size() > count) {
			products = products.subList(0, count);
		}
		products.sort(new Product.By_Count(SortOrder.Descending));

		dto.put("popularProducts", products);

		cacheLayer.add(key(shop_profile, site), dto, 10, TimeUnit.SECONDS);

		return dto;
	}

	@GET
	@Path("/userprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO userprofile(@QueryParam("userid") final String userid, @QueryParam("count") @DefaultValue("10") int count) {

		final ProfileDTO dto = new ProfileDTO();
		if (Utils.isNullOrEmpty(userid)) {
			dto.put("error", true);
			dto.put("message", "missing required parameters");
			return dto;
		}
		
		if (cacheLayer.exists(key(user_profile, userid))) {
			Optional<ProfileDTO> cacheValue = cacheLayer.get(key(user_profile, userid), ProfileDTO.class);
			if (cacheValue.isPresent()) {
				return cacheValue.get();
			}
		}


		var recentlyViewProducts = new RecentlyViewedProducts();
		var frequentlyPurchasedProducts = new FrequentlyPurchasedProducts();
		ProfileGenerator userProfile = ProfileGenerator.builder(analyticsDB, userid, ProfileGenerator.Type.USER)
				.addCollector(recentlyViewProducts)
				.addCollector(frequentlyPurchasedProducts)
				.build();
		System.out.println("before generate");
		userProfile.generate();
		System.out.println("after generate");
		List<Product> rvProducts = recentlyViewProducts.getProducts();
		System.out.println("products: " + rvProducts.size());
		if (rvProducts.size() > 10) {
			rvProducts = rvProducts.subList(0, 10);
		}
		rvProducts.sort(new Product.By_Date(SortOrder.Descending));
		dto.put("recentlyViewedProducts", rvProducts);

		List<Product> fpProducts = frequentlyPurchasedProducts.getProducts();
		if (fpProducts.size() > count) {
			fpProducts = fpProducts.subList(0, count);
		}
		fpProducts.sort(new Product.By_Count(SortOrder.Descending));
		dto.put("frequentlyPurchasedProducts", new ArrayList<>(fpProducts));

		cacheLayer.add(key(user_profile, userid), dto, 10, TimeUnit.SECONDS);

		return dto;
	}

	@Path("/recommendations")
	public RecommendationResources recommendations() {
		return new RecommendationResources(analyticsDB, cacheLayer);
	}

}
