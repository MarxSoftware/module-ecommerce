/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.strategies;

import com.thorstenmarx.webtools.modules.ecommerce.Utils;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Item;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Order;
import com.thorstenmarx.webtools.modules.ecommerce.profile.recommendation.Strategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

/**
 *
 * @author marx
 */
public class UserStrategy implements Strategy<String> {

	public final double SIMILARITY_THRESHOLD = 0.7;

	final Map<Integer, Order> orders;

	public UserStrategy(final Map<Integer, Order> orders) {
		this.orders = orders;
	}

	@Override
	public List<Item> calculate(final String user_id) {
		if (Utils.isNullOrEmpty(user_id)) {
			return Collections.emptyList();
		}

		final Map<String, List<Item>> user_items = new HashMap<>();
		final MutableMap<String, Rating> user_rating;
		final MutableMap<String, Double> user_similarity;

		orders.values().stream().forEach((order) -> {
			if (!user_items.containsKey(order.user_id)) {
				user_items.put(order.user_id, new ArrayList<>());
			}
			user_items.get(order.user_id).addAll(order.items);
		});

		if (!user_items.containsKey(user_id)) {
			return Collections.EMPTY_LIST;
		}
		
		List<Item> test_user_items = user_items.get(user_id);

		
		Map<String, List<Item>> neighborhood = createNeighborhood(user_id, test_user_items, user_items);

		user_rating = generateRatings(neighborhood, user_id, test_user_items);
		user_similarity = generateSimilarity(user_rating, test_user_items);

		List<Item> recommendations = createRecommendations(test_user_items, user_similarity, neighborhood);

		System.out.println("normalized user similarity");
		System.out.println(user_similarity);
		System.out.println("recommendations");
		System.out.println(recommendations);

		return recommendations;
	}

	private List<Item> createRecommendations(final List<Item> test_user_items, final MutableMap<String, Double> user_similarity, final Map<String, List<Item>> neighborhood) {
		Map<Integer, Item> recom_items = Maps.mutable.empty();
		user_similarity.entrySet().stream()
				.filter((entry) -> entry.getValue() >= SIMILARITY_THRESHOLD)
				.forEach((entry) -> {
					List<Item> items = neighborhood.get(entry.getKey());
					Collection<Item> subtract = CollectionUtils.subtract(items, test_user_items);
					subtract.forEach((item) -> {
						Item temp;
						if (recom_items.containsKey(item.id)) {
							temp = recom_items.get(item.id);
						} else {
							temp = new Item(item.id);
						}
						temp.count.addAndGet(item.getIntCount());
						recom_items.put(temp.id, temp);
					});
				});
		
		Comparator<Map.Entry<Integer, Item>> reverseOrderComparator = Collections.reverseOrder(Comparator.comparingInt((final Map.Entry value) -> ((Item)value.getValue()).getIntCount()));
		return recom_items.entrySet().stream().sorted(reverseOrderComparator).map((entry) -> entry.getValue()).collect(Collectors.toList());
	}

	private MutableMap<String, Double> generateSimilarity(final MutableMap<String, Rating> user_rating, List<Item> test_user_items) {
		final MutableMap<String, Double> user_similarity = Maps.mutable.empty();
		user_rating.forEach((id, rating) -> {
			user_similarity.put(id, rating.rating(test_user_items.size()));
		});

		return user_similarity;
	}

	private MutableMap<String, Rating> generateRatings(Map<String, List<Item>> neighborhood, final String user_id, List<Item> test_user_items) {
		final MutableMap<String, Rating> user_rating = Maps.mutable.empty();
		neighborhood.entrySet().forEach((entry) -> {
			if (!entry.getKey().equals(user_id)) {
				Collection<Item> retainAll = CollectionUtils.retainAll(test_user_items, entry.getValue());
				Collection<Item> subtract = CollectionUtils.subtract(entry.getValue(), test_user_items);

				Rating current_value;
				if (user_rating.contains(entry.getKey())) {
					current_value = user_rating.get(entry.getKey());
				} else {
					current_value = new Rating();
				}
				current_value.same_items += retainAll.size();
				current_value.different_items += subtract.size();
				current_value.items += entry.getValue().size();
				user_rating.put(entry.getKey(), current_value);

			}
		});

		return user_rating;
	}

	/**
	 * Returns the List of neighbors neighbors = users ordert at least a single
	 * item of the target user
	 *
	 * @param test_user_items
	 * @param user_items
	 * @return
	 */
	private Map<String, List<Item>> createNeighborhood(final String test_user, final List<Item> test_user_items, final Map<String, List<Item>> user_items) {
		return user_items.entrySet().stream().filter(entry -> {
			return !test_user.equals(entry.getKey())
					&& CollectionUtils.containsAny(test_user_items, entry.getValue());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public static class Rating {

		public int same_items = 0;
		public int different_items = 0;
		public int items = 0;

		public double rating(final int target_items) {
			return Utils.normalize(same_items, target_items)
					+ Utils.normalize(different_items, target_items);
		}
	}
}
