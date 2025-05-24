package com.github.rcd47.x2data.explorer.jfx.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SeparatorMenuItem;

public class MultiSelectMenu<E> extends MenuButton {
	
	public MultiSelectMenu(String itemsDescription, Collection<E> options, Function<E, String> stringifier,
			Consumer<Set<E>> changeListener) {
		this(itemsDescription, options, stringifier, changeListener, (_, _) -> true);
	}
	
	public MultiSelectMenu(String itemsDescription, Collection<E> options, Function<E, String> stringifier,
			BiPredicate<E, BooleanProperty> selectBind) {
		this(itemsDescription, options, stringifier, _ -> {}, selectBind);
	}
	
	private MultiSelectMenu(String itemsDescription, Collection<E> options, Function<E, String> stringifier,
			Consumer<Set<E>> changeListener, BiPredicate<E, BooleanProperty> selectBind) {
		var items = getItems();
		var labels = new ArrayList<Label>(options.size() + 2);
		var selectedOptions = new ArrayList<BooleanProperty>(options.size());
		
		// not using ObservableSet because its addAll() and clear() fire a change for each item
		// that creates perf problems if there are a lot of items
		var selectedSet = new HashSet<E>();
		
		var selectAllLabel = new Label("Select All");
		labels.add(selectAllLabel);
		var selectAllItem = new CustomMenuItem(selectAllLabel, false);
		selectAllItem.setOnAction(_ -> {
			selectedSet.addAll(options);
			changeListener.accept(selectedSet);
			for (var boolProp : selectedOptions) {
				boolProp.setValue(true);
			}
		});
		items.add(selectAllItem);
		
		var clearAllLabel = new Label("Clear All");
		labels.add(clearAllLabel);
		var clearAllItem = new CustomMenuItem(clearAllLabel, false);
		clearAllItem.setOnAction(_ -> {
			selectedSet.clear();
			changeListener.accept(selectedSet);
			for (var boolProp : selectedOptions) {
				boolProp.setValue(false);
			}
		});
		items.add(clearAllItem);
		
		items.add(new SeparatorMenuItem());
		
		for (var option : options) {
			var boolProp = new SimpleBooleanProperty(false);
			boolProp.addListener((_, _, newVal) -> {
				if (newVal) {
					if (selectedSet.add(option)) {
						changeListener.accept(selectedSet);
					}
				} else {
					if (selectedSet.remove(option)) {
						changeListener.accept(selectedSet);
					}
				}
			});
			boolProp.setValue(selectBind.test(option, boolProp));
			selectedOptions.add(boolProp);
			
			var label = new Label(stringifier.apply(option));
			label.styleProperty().bind(boolProp.map(b -> b ? "-fx-font-weight: bold" : null));
			labels.add(label);
			
			var menuItem = new CustomMenuItem(label, false);
			menuItem.setOnAction(_ -> boolProp.setValue(!boolProp.getValue()));
			
			items.add(menuItem);
		}
		
		textProperty().bind(Bindings.createStringBinding(
				() -> {
					var numSelected = selectedOptions.stream().filter(b -> b.getValue()).count();
					var numOptions = selectedOptions.size();
					return (numSelected == numOptions ? "All" : numSelected + " / " + numOptions) + " " + itemsDescription;
				},
				selectedOptions.toArray(BooleanProperty[]::new)));
		
		// make all labels have the width of the widest one
		// else clicking on the empty space to the right of shorter labels doesn't do anything
		setOnShown(_ -> {
			var max = labels.stream().mapToDouble(Label::getWidth).max().orElse(0);
			for (var label : labels) {
				label.setPrefWidth(max);
			}
		});
	}

}
