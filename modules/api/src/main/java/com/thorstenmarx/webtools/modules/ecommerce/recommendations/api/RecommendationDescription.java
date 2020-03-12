package com.thorstenmarx.webtools.modules.ecommerce.recommendations.api;

/*-
 * #%L
 * recommendations-api
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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marx
 */
public class RecommendationDescription implements Serializable{

	private static final long serialVersionUID = 1546416706819442546L;

	public enum Type {
		Item,
		User;
	}

	private String id;

	private String name;

	private String event;

	private String userIdField;

	private String itemIdField;

	private TimeRange timeRange = new TimeRange();
	
	private Type type = Type.User;

	private Map<String, String> filter = new HashMap<>();

	public RecommendationDescription() {

	}

	public Type getType() {
		return type;
	}

	public RecommendationDescription setType(Type type) {
		this.type = type;
		return this;
	}

	
	
	public String getId() {
		return id;
	}

	public RecommendationDescription setId(String id) {
		this.id = id;
		return this;
	}

	public String getEvent() {
		return event;
	}

	public RecommendationDescription setEvent(String event) {
		this.event = event;
		return this;
	}

	public String getName() {
		return name;
	}

	public RecommendationDescription setName(String name) {
		this.name = name;
		return this;
	}

	public String getUserIdField() {
		return userIdField;
	}

	public RecommendationDescription setUserIdField(String userIdField) {
		this.userIdField = userIdField;
		return this;
	}

	public String getItemIdField() {
		return itemIdField;
	}

	public RecommendationDescription setItemIdField(String itemIdField) {
		this.itemIdField = itemIdField;
		return this;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public RecommendationDescription setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
		return this;
	}

	public Map<String, String> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, String> filter) {
		this.filter = filter;
	}

}
