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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class DescriptionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionService.class);

	public static final String FILENAME = "descriptions.xml";
	String path;

	Descriptions descriptions = new Descriptions();

	private final Map<String, RecommendationDescription> descriptionMap = new ConcurrentHashMap<>();

	private List<ChangedEventListener> listeners = new CopyOnWriteArrayList();

	public DescriptionService(final String path) {
		this.path = path;
		if (!path.endsWith("/")) {
			this.path += "/";
		}

		loadDescriptions();
	}

	public synchronized void addEventListener(final ChangedEventListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeEventListener(final ChangedEventListener listener) {
		listeners.remove(listener);
	}

	public void add(RecommendationDescription description) {
		this.descriptionMap.put(description.getId(), description);
		this.descriptions.addAll(this.descriptionMap.values());
		saveSites();
		fireEvent(new ChangedEvent(this, ChangedEvent.Type.Update, description));
	}

	public void remove(String id) {
		RecommendationDescription description = this.descriptionMap.remove(id);
		this.descriptions.addAll(this.descriptionMap.values());
		saveSites();
		fireEvent(new ChangedEvent(this, ChangedEvent.Type.Delete, description));
	}

	public RecommendationDescription get(String id) {
		RecommendationDescription desc = descriptionMap.get(id);
		if (desc == null) {
			return null;
		}
		return Clone.clone(desc);
	}

	public Collection<RecommendationDescription> all() {
		Collection<RecommendationDescription> result = Clone.clone(descriptionMap.values());
		
		return Collections.unmodifiableCollection(result);
	}

	private void loadDescriptions() {
//		try {
//			File file = new File(path, FILENAME);
//
//			if (!file.exists()) {
//				return;
//			}
//
//			JAXBContext jaxbContext = JAXBContext.newInstance(Descriptions.class);
//			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//
//			this.descriptions = (Descriptions) jaxbUnmarshaller.unmarshal(file);
//
//			this.descriptions.descriptions.stream().forEach((s) -> {
//				descriptionMap.put(s.getId(), s);
//			});
//		} catch (JAXBException ex) {
//			LOGGER.error("", ex);
//			throw new RuntimeException(ex);
//		}

	}

	private synchronized void saveSites() {
//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance(Descriptions.class);
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			jaxbMarshaller.marshal(this.descriptions, new File(path, FILENAME));
//		} catch (JAXBException ex) {
//			LOGGER.error("", ex);
//		}
	}

	private synchronized void fireEvent(ChangedEvent event) {
		listeners.forEach((eh) -> {
			eh.changed(event);
		});
	}

	public static class Descriptions {

		List<RecommendationDescription> descriptions = new ArrayList<>();

		public List<RecommendationDescription> getDescriptions() {
			return descriptions;
		}

		public void addAll(Collection<RecommendationDescription> values) {
			this.descriptions.clear();
			this.descriptions.addAll(values);
		}

		public void setDescriptions(List<RecommendationDescription> description) {
			this.descriptions = description;
		}
	}

	public static interface ChangedEventListener {

		public void changed(ChangedEvent event);
	}

	public static class ChangedEvent extends EventObject {

		private static final long serialVersionUID = -8144811252269149761L;

		public enum Type {
			Update, Delete
		}

		private Type type;
		final RecommendationDescription description;

		public ChangedEvent(final Object source, final Type type, final RecommendationDescription description) {
			super(source);
			this.type = type;
			this.description = description;
		}

		public RecommendationDescription description() {
			return description;
		}

		public Type type() {
			return type;
		}
	}

}
