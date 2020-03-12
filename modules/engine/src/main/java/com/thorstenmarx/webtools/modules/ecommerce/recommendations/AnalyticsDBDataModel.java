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
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.api.RecommendationDescription;
import com.thorstenmarx.webtools.modules.ecommerce.recommendations.utils.Duplicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class AnalyticsDBDataModel implements DataModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsDBDataModel.class);
	private static final long serialVersionUID = -1573564219784200999L;

	final AnalyticsDB analyticsDb;

	private DataModel delegate;

	final RecommendationDescription description;

	final MemoryIDMigrator idMigrator = new MemoryIDMigrator();

	public AnalyticsDBDataModel(final AnalyticsDB analyticsDb, final RecommendationDescription description) {
		this.analyticsDb = analyticsDb;
		this.description = description;
		buildModel();
	}

	private void buildModel() {
		Query.Builder queryBuilder = Query.builder()
				.term(Fields.Event.value(), description.getEvent());

		if (description.getTimeRange() != null) {
			queryBuilder.start(description.getTimeRange().getStart())
					.end(description.getTimeRange().getEnd());
		}

		if (!description.getFilter().isEmpty()) {
			for (Map.Entry<String, String> entry : description.getFilter().entrySet()) {
				queryBuilder.term(entry.getKey(), entry.getValue());
			}
		}

		Query query = queryBuilder.build();

		Future<GenericDataModel> request = analyticsDb.query(query, new Aggregator<GenericDataModel>() {
			@Override
			public GenericDataModel call() throws Exception {
				Duplicates duplicates = new Duplicates();
				FastByIDMap<Collection<Preference>> userIDPrefMap = new FastByIDMap<>();
				float ratingValue = 1f;
				documents.forEach((doc) -> {
					if (!doc.document.containsKey(description.getUserIdField())
							|| !doc.document.containsKey(description.getItemIdField())) {
						return;
					}

					Object idField = doc.document.get(description.getUserIdField());
//					IndexableField userField = doc.document.getField(description.getUserIdField());
					long userID = getLongId(idField);

					if (doc.document.containsKey(description.getItemIdField())) {
						Object fields = doc.document.get(description.getItemIdField());
						if (fields instanceof List) {
							for (Object field : ((List) fields).toArray()) {
								long itemID = getLongId(field);
								Collection<Preference> userPrefs = userIDPrefMap.get(userID);
								if (userPrefs == null) {
									userPrefs = new ArrayList<>(2);
									userIDPrefMap.put(userID, userPrefs);
								}
								if (!duplicates.isDuplicate(userID, itemID)) {
									userPrefs.add(new GenericPreference(userID, itemID, ratingValue));
								}
							}
						} else {
							long itemID = getLongId(fields);
							Collection<Preference> userPrefs = userIDPrefMap.get(userID);
							if (userPrefs == null) {
								userPrefs = new ArrayList<>(2);
								userIDPrefMap.put(userID, userPrefs);
							}
							if (!duplicates.isDuplicate(userID, itemID)) {
								userPrefs.add(new GenericPreference(userID, itemID, ratingValue));
							}
//							userPrefs.add(new GenericPreference(userID, itemID, ratingValue));
						}
					}
				});
				userIDPrefMap.rehash();
				return new GenericDataModel(GenericDataModel.toDataMap(userIDPrefMap, true));
			}
		});

		try {
			this.delegate = request.get();
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.error(null, ex);
		}
	}

	public long getLongId(Object field) {
		Long id = null;
		String sid = null;
		if (field instanceof String) {
			sid = (String) field;
		} else if (field instanceof Integer) {
			sid = "" + (Integer) field;
		} else if (field instanceof Float) {
			sid = "" + (Float) field;
		} else if (field instanceof Double) {
			sid = "" + (Double) field;
		} else if (field instanceof Long) {
			sid = "" + (Long) field;
		}
		if (sid == null) {
			throw new RuntimeException("unable to get id + " + field + " / " + field.getClass().getName());
		}
		if (id == null) {
			id = idMigrator.toLongID(sid);
		}
		idMigrator.storeMapping(id, sid);

		return id;
	}

	public String getStringId(long id) {
		return idMigrator.toStringID(id);
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		buildModel();
	}

	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		return delegate.getUserIDs();
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {
		return delegate.getPreferencesFromUser(userID);
	}

	@Override
	public FastIDSet getItemIDsFromUser(long userID) throws TasteException {
		return delegate.getItemIDsFromUser(userID);
	}

	@Override
	public LongPrimitiveIterator getItemIDs() throws TasteException {
		return delegate.getItemIDs();
	}

	@Override
	public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {
		return delegate.getPreferencesForItem(itemID);
	}

	@Override
	public Float getPreferenceValue(long userID, long itemID) throws TasteException {
		return delegate.getPreferenceValue(userID, itemID);
	}

	@Override
	public Long getPreferenceTime(long userID, long itemID) throws TasteException {
		return delegate.getPreferenceTime(userID, itemID);
	}

	@Override
	public int getNumItems() throws TasteException {
		return delegate.getNumItems();
	}

	@Override
	public int getNumUsers() throws TasteException {
		return delegate.getNumUsers();
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID) throws TasteException {
		return delegate.getNumUsersWithPreferenceFor(itemID);
	}

	@Override
	public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException {
		return delegate.getNumUsersWithPreferenceFor(itemID1, itemID2);
	}

	@Override
	public void setPreference(long userID, long itemID, float value) throws TasteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removePreference(long userID, long itemID) throws TasteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean hasPreferenceValues() {
		return delegate.hasPreferenceValues();
	}

	@Override
	public float getMaxPreference() {
		return delegate.getMaxPreference();
	}

	@Override
	public float getMinPreference() {
		return delegate.getMinPreference();
	}

	@Override
	public String toString() {
		return "AnalyticsDBDataModel";
	}

}
