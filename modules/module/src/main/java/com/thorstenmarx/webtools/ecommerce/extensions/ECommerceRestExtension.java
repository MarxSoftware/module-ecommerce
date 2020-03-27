/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.ecommerce.extensions;

import com.thorstenmarx.modules.api.annotation.Extension;
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.cache.CacheLayer;
import com.thorstenmarx.webtools.api.extensions.SecureRestResourceExtension;
import com.thorstenmarx.webtools.modules.ecommerce.SortOrder;
import com.thorstenmarx.webtools.modules.ecommerce.profile.ProfileGenerator;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.FrequentlyPurchasedProducts;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.Product;
import com.thorstenmarx.webtools.modules.ecommerce.profile.collectors.RecentlyViewedProducts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
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

	@Override
	public void init() {
	}

	private String key (final String prefix, final String site) {
		return String.format("ecommerce-%s-%s", prefix, site);
	}
	
	@GET
	@Path("/shopprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO shopprofile (@QueryParam("site") final String siteid) {
		
		if (cacheLayer.exists(key("shopprofile", siteid))){
			Optional<ProfileDTO> cacheValue = cacheLayer.get(key("shopprofile", siteid),  ProfileDTO.class);
			if (cacheValue.isPresent()){
				return cacheValue.get();
			}
		}
		
		var frequentlyPurchasedProducts = new FrequentlyPurchasedProducts();
		ProfileGenerator profileGenerator = ProfileGenerator.builder(analyticsDB, siteid, ProfileGenerator.Type.SHOP)
				.addCollector(frequentlyPurchasedProducts)
				.build();
		
		profileGenerator.generate();
		
		ProfileDTO profileDto = new ProfileDTO();
		
		List<Product> products = frequentlyPurchasedProducts.getProducts();
		if (products.size() > 10) {
			products = products.subList(0, 10);
		}
		products.sort(new Product.By_Count(SortOrder.Descending));
		
		profileDto.put("popularProducts", products);
		
		cacheLayer.add(key("shopprofile", siteid), profileDto, 30, TimeUnit.SECONDS);
		
		return profileDto;
	}
	
	@GET
	@Path("/userprofile")
	@Produces(MediaType.APPLICATION_JSON)
	public ProfileDTO userprofile(@QueryParam("userid") final String userid) {
		System.out.println("get the userprofile for " + userid);
		final ProfileDTO dto = new ProfileDTO();

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
		if (fpProducts.size() > 10) {
			fpProducts = fpProducts.subList(0, 10);
		}
		fpProducts.sort(new Product.By_Count(SortOrder.Descending));
		dto.put("frequentlyPurchasedProducts", new ArrayList<>(fpProducts));

		return dto;
	}
	
	@Path("/recommendations")
	public RecommendationResources recommendations() {
		return new RecommendationResources(analyticsDB);
	}
	
	
}
