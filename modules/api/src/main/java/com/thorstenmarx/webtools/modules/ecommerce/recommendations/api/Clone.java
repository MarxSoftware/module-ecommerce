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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thmarx
 */
public class Clone {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Clone.class);

	/**
	 * This method makes a "deep clone" of any Java object it is given.
	 * @param <T>
	 * @param object
	 * @return 
	 */
	public static <T> T clone(T object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new RuntimeException(e.toString());
		}
	}

	public static <T> Collection<T> clone(Collection<T> objects) {
		if (objects == null || objects.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<T> result = new ArrayList<>();

		objects.forEach((object) -> {
			result.add(Clone.clone(object));
		});

		return result;
	}
}
