package com.github.rcd47.x2data.lib.unreal.mappings;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface UnrealStaticArraySize {
	
	/**
	 * Number of elements in the static array.
	 */
	int value();
	
}
