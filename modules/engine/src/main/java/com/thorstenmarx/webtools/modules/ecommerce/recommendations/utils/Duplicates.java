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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author marx
 */
public class Duplicates {
	private final Set<Tuple> tuples = new HashSet<>();
	
	/**
	 * Check, if the tuple pf the to lons is already used, if not, it is added to the used tuples.
	 * 
	 * @param first
	 * @param second
	 * @return 
	 */
	public boolean isDuplicate (final long first, final long second) {
		Tuple t = Tuple.of(first, second);
		boolean contains = tuples.contains(t);
		if (!contains) {
			tuples.add(t);
		}
		return contains;
	}
	
	private static class Tuple {
		final long first; 
		final long second;

		public static Tuple of (final long first, final long second){
			return new Tuple(first, second);
		}
		
		private Tuple(long first, long second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public int hashCode() {
			int hash = 5;
			hash = 79 * hash + (int) (this.first ^ (this.first >>> 32));
			hash = 79 * hash + (int) (this.second ^ (this.second >>> 32));
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Tuple other = (Tuple) obj;
			if (this.first != other.first) {
				return false;
			}
			if (this.second != other.second) {
				return false;
			}
			return true;
		}
		
		
	}
}
