package com.thorstenmarx.webtools.modules.ecommerce.recommendations.utils;

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
/**
 *
 * @author marx
 */
public abstract class Hasher {

	/**
	 * 
	 * @param string
	 * @return 
	 */
	public static long hash(String string) {
		long h = 1125899906842597L; // prime
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}
}
