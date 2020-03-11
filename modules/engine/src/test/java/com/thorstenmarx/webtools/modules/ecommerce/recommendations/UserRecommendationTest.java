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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.Interval;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.RecommendationDescription;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.TimeRange;
import com.thorstenmarx.webtools.test.MockAnalyticsDB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class UserRecommendationTest {

	@Test
	public void simpleTest() throws TasteException {
		Multimap<String, String> userItems = ArrayListMultimap.create();
		user(userItems, "u1", "p1", "p2", "p3", "p4");
		user(userItems, "u2", "p1", "p4");
		user(userItems, "u3", "p1", "p2", "p4");
		user(userItems, "u4", "p2", "p3");
		user(userItems, "u5", "p1", "p3", "p4");

		UserBasedRecommender recommender = createRecommender(userItems);

		AnalyticsDBDataModel model = (AnalyticsDBDataModel) recommender.getDataModel();
		List<RecommendedItem> recommendations = recommender.recommend(model.idMigrator.toLongID("u2"), 3);
		List<String> recIds = new ArrayList<>();
		for (RecommendedItem recommendation : recommendations) {
			recIds.add(model.idMigrator.toStringID(recommendation.getItemID()));
		}
		Assertions.assertThat(recIds).contains("p2", "p3").hasSize(2);
	}

	private void user(Multimap<String, String> map, final String user, final String... items) {
		map.putAll(user, Arrays.asList(items));
	}

	private UserBasedRecommender createRecommender(final Multimap<String, String> data) throws TasteException {

		MockAnalyticsDB analyticsDb = new MockAnalyticsDB();
//		analyticsDb.documents(EngineTest.createDocuments(data));
		EngineTest.filldb(data, analyticsDb);

		RecommendationDescription description = new RecommendationDescription()
				.setId("eins")
				.setName("user also bought")
				.setItemIdField("c_order_items")
				.setUserIdField("user");
		TimeRange timeRange = new TimeRange().setCount(2).setInterval(Interval.DAY);
		description.setTimeRange(timeRange);
		DataModel model = new AnalyticsDBDataModel(analyticsDb, description);
//		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserSimilarity similarity = new UncenteredCosineSimilarity(model);
//		UserSimilarity similarity = new SpearmanCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

		return recommender;
	}

}
