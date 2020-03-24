/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile;

import com.thorstenmarx.webtools.api.analytics.AnalyticsDB;
import com.thorstenmarx.webtools.api.analytics.Fields;
import com.thorstenmarx.webtools.api.analytics.query.Aggregator;
import com.thorstenmarx.webtools.api.analytics.query.Query;
import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;
import com.thorstenmarx.webtools.modules.ecommerce.Constants;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class ProfileGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileGenerator.class);

	public enum Type {
		USER,
		SHOP
	}
	
	private final Set<Collector> collectors;

	private final String id;

	private final AnalyticsDB analyticsDB;
	
	private final Type type;
	
	private final String event;

	private ProfileGenerator(final Builder builder) {
		this.collectors = builder.collectors;
		this.id = builder.id;
		this.analyticsDB = builder.analyticsDB;
		this.type = builder.type;
		this.event = builder.event;
	}

	private void callCollectors (final ShardDocument document) {
		collectors.forEach((collector) -> {
			collector.handle(document);
		});
	}
	public void generate() {
		try {
			Query.Builder queryBuilder = Query.builder()
					.start(0).end(Long.MAX_VALUE);
			
			if (Type.USER.equals(type)) {
				queryBuilder.term(Fields.UserId.value(), id);
			} else if (Type.SHOP.equals(type)) {
				queryBuilder.term(Fields.Site.value(), id);
			}
			if (event != null) {
				queryBuilder.term(Fields.Event.value(), event);
			}
			
			
			final Query query = queryBuilder.build();
			CompletableFuture<Void> queryResult = analyticsDB.query(query, new Aggregator<Void>() {
				@Override
				public Void call() throws Exception {
					documents.forEach(ProfileGenerator.this::callCollectors);
					return null;
				}
			});
			queryResult.get();
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.error("", ex);
		}
	}

	public static Builder builder(final AnalyticsDB analyticsDB, final String userid, final Type type) {
		return new Builder(analyticsDB, userid, type);
	}

	public static class Builder {

		private Set<Collector> collectors;

		private final AnalyticsDB analyticsDB;
		
		private final String id;
		
		private final Type type;
		
		private String event;

		protected Builder(final AnalyticsDB analyticsDB, final String id, final Type type) {
			this.analyticsDB = analyticsDB;
			this.id = id;
			this.collectors = new HashSet<>();
			this.type = type;
		}

		public Builder event (final String event) {
			this.event = event;
			return this;
		}
		
		public Builder addCollector(final Collector collector) {
			collectors.add(collector);
			return this;
		}

		public ProfileGenerator build() {
			return new ProfileGenerator(this);
		}
	}
}
