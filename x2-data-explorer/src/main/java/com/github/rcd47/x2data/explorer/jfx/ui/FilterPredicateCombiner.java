package com.github.rcd47.x2data.explorer.jfx.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;

public class FilterPredicateCombiner<T> {

	private final SimpleObjectProperty<Predicate<T>> property;
	private final List<Predicate<T>> filters; // would prefer an array but can't create arrays of generic types
	
	public FilterPredicateCombiner(int filterCount) {
		property = new SimpleObjectProperty<>(o -> true);
		filters = new ArrayList<>(filterCount);
		for (int i = 0; i < filterCount; i++) {
			filters.add(null);
		}
	}
	
	public void setFilter(int index, Predicate<T> filter) {
		filters.set(index, filter);
		property.set(filters.stream().filter(f -> f != null).reduce((a, b) -> a.and(b)).orElse(o -> true));
	}

	public SimpleObjectProperty<Predicate<T>> getProperty() {
		return property;
	}
	
}
