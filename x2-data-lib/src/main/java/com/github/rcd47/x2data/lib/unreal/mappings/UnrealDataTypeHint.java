package com.github.rcd47.x2data.lib.unreal.mappings;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.rcd47.x2data.lib.unreal.UnrealDataType;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, TYPE_USE })
public @interface UnrealDataTypeHint {
	
	UnrealDataType value();
	
}
