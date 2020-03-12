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
import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.RecommendationDescription;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author marx
 */
public class Engine /*implements DescriptionService.ChangedEventListener*/ {

	final AnalyticsDB analyticsDb;

	private final Map<String, Recommendation> recommendations = new ConcurrentHashMap<>();

	public Engine(final AnalyticsDB analyticsDb) {
		this.analyticsDb = analyticsDb;
	}

	public void addRecommendationDescription (final RecommendationDescription description) {
		recommendations.put(description.getId(), new Recommendation(description, analyticsDb));
	}

	public void close() {
		recommendations.values().forEach((r) -> {
			r.close();
		});
		recommendations.clear();
	}
	public void refresh() {
		recommendations.values().forEach((r) -> {
			r.refresh();
		});
	}

	public Recommendation recommendation(final String id) {
		return recommendations.get(id);
	}
}
