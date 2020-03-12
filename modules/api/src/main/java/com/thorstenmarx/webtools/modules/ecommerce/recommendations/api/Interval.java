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
public enum Interval implements Serializable {
	DAY,
	WEEK,
	MONTH,
	YEAR;

	public TimeWindow.UNIT getTimeUnit() {
		switch (this) {
			case DAY:
				return TimeWindow.UNIT.DAY;
			case WEEK:
				return TimeWindow.UNIT.WEEK;
			case MONTH:
				return TimeWindow.UNIT.MONTH;
			case YEAR:
				return TimeWindow.UNIT.YEAR;
		}
		throw new IllegalArgumentException("no valid interval");
	}
}
