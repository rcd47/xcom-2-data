package com.github.rcd47.x2data.explorer.jfx.ui;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import com.google.common.base.Strings;

import groovy.lang.GroovyShell;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class ExpressionFilter<T> {
	
	private static final GroovyShell GROOVY = new GroovyShell();
	
	private final TextField input;
	
	public ExpressionFilter(FilterPredicateCombiner<T> combiner, int combinerIndex, String variableName) {
		input = new TextField();
		input.setPrefColumnCount(30);
		input.setPromptText("Type expression and hit enter");
		input.textProperty().addListener((_, _, _) -> setBackgroundColor("lightskyblue"));
		input.setOnAction(_ -> {
			var text = input.getText();
			if (Strings.isNullOrEmpty(text)) {
				combiner.setFilter(combinerIndex, _ -> true);
				setBackgroundColor(null);
			} else {
				try {
					var script = GROOVY.parse(input.getText());
					combiner.setFilter(
							combinerIndex,
							v -> {
								script.getBinding().setVariable(variableName, v);
								try {
									return DefaultTypeTransformation.castToBoolean(script.run());
								} catch (Exception e) {
									return false;
								}
							});
					setBackgroundColor("lightgreen");
				} catch (CompilationFailedException e) {
					setBackgroundColor("lightpink");
				}
			}
		});
	}
	
	private void setBackgroundColor(String color) {
		input.setStyle(color == null ? null : "-fx-control-inner-background: " + color);
	}
	
	public Node getNode() {
		return input;
	}
	
}
