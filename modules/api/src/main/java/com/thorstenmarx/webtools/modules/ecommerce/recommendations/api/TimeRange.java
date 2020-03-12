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
import com.thorstenmarx.webtools.api.TimeWindow;
import java.io.Serializable;

/**
 *
 * @author marx
 */
public class TimeRange implements Serializable {

	private static final long serialVersionUID = 5534661421571660787L;
	private Interval interval = Interval.DAY;
	private int count;

	public Interval getInterval() {
		return interval;
	}

	public TimeRange setInterval(Interval interval) {
		this.interval = interval;
		return this;
	}

	public int getCount() {
		return count;
	}

	public TimeRange setCount(int count) {
		this.count = count;
		return this;
	}
	
	
	public long getStart () {
		return System.currentTimeMillis() - new TimeWindow(interval.getTimeUnit(), count).millis();
	}
	
	public long getEnd () {
		return System.currentTimeMillis();
	}
}
