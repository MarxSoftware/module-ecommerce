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
import java.util.ArrayList;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public final class Recommendation {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Recommendation.class);

	final RecommendationDescription description;
	final AnalyticsDB analyticsDb;

	AnalyticsDBDataModel model;

	Recommender recommender;

	public Recommendation(final RecommendationDescription description, final AnalyticsDB analyticsDb) {
		this.description = description;
		this.analyticsDb = analyticsDb;

		try {
			init();
		} catch (TasteException ex) {
			LOGGER.error(null, ex);
			throw new RuntimeException(ex);
		}
	}

	private void init() throws TasteException {
		this.model = new AnalyticsDBDataModel(analyticsDb, description);

		if (description.getType().equals(RecommendationDescription.Type.Item)) {
			ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
			this.recommender = new GenericItemBasedRecommender(model, similarity);
		} else if (description.getType().equals(RecommendationDescription.Type.User)) {
			UserSimilarity similarity = new UncenteredCosineSimilarity(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
			this.recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		}
	}

	public List<String> recommend(final String id, final int count) {
		List<String> result = new ArrayList<>();

		List<RecommendedItem> recommendations = null;
		try {
			if (description.getType().equals(RecommendationDescription.Type.Item)) {
				recommendations = ((ItemBasedRecommender) recommender).mostSimilarItems(model.idMigrator.toLongID(id), count);
			} else if (description.getType().equals(RecommendationDescription.Type.User)) {
				recommendations = ((UserBasedRecommender) recommender).recommend(model.idMigrator.toLongID(id), count);
			}
		} catch (TasteException ex) {
			LOGGER.error(null, ex);
		}
		if (recommendations != null) {
			recommendations.forEach((rc) -> {
				result.add(((AnalyticsDBDataModel)recommender.getDataModel()).idMigrator.toStringID(rc.getItemID()));
			});
		}

		return result;
	}
	
	void close () {
		this.recommender = null;
	}

	void refresh () {
		this.model.refresh(null);
	}
}
