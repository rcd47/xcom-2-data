package com.github.rcd47.x2data.lib.unreal.typings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;
import org.jboss.jandex.Type;
import org.jboss.jandex.Type.Kind;

import com.github.rcd47.x2data.lib.savegame.X2SaveGameHeader;
import com.github.rcd47.x2data.lib.unreal.UnrealDataType;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealDataTypeHint;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealTypeName;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedProperty;
import com.github.rcd47.x2data.lib.unreal.mappings.UnrealUntypedStruct;
import com.github.rcd47.x2data.lib.unreal.mappings.XComSingletonStateType;

public class UnrealTypingsBuilder {
	
	private static final String PACKAGE_PREFIX = "com.github.rcd47.x2data.lib.unreal.mappings.";
	private static final DotName TYPE_HINT_ANNOTATION = DotName.createSimple(UnrealDataTypeHint.class.getName());
	private static final DotName TYPE_NAME_ANNOTATION = DotName.createSimple(UnrealTypeName.class.getName());
	private static final DotName UNTYPED_PROPERTY_ANNOTATION = DotName.createSimple(UnrealUntypedProperty.class.getName());
	private static final DotName UNTYPED_STRUCT_ANNOTATION = DotName.createSimple(UnrealUntypedStruct.class.getName());
	private static final DotName SINGLETON_STATE_ANNOTATION = DotName.createSimple(XComSingletonStateType.class.getName());
	private static final Map<String, String> DLC_PACKAGES = Map.ofEntries(
			Map.entry("CovertInfiltration", "covertinf"),
			Map.entry("DarkXCOMRedux", "mocx"),
			Map.entry("GrimHorizonFix", "ghfix"),
			Map.entry("PauseWorldTime", "pauseworldtime"),
			Map.entry("X2Statistics", "x2stats"));
	
	public Map<String, UnrealTypeInformer> build(X2SaveGameHeader saveHeader) {
		return build(saveHeader.installedDlcAndMods.stream().map(d -> d.internalName).collect(Collectors.toSet()));
	}
	
	public Map<String, UnrealTypeInformer> build(Collection<String> dlcInternalNames) {
		// determine which packages to look at
		Set<String> packagePrefixes = new HashSet<>();
		packagePrefixes.add(PACKAGE_PREFIX + "base");
		for (String dlcName : dlcInternalNames) {
			var packageName = DLC_PACKAGES.get(dlcName);
			if (packageName != null) {
				packagePrefixes.add(PACKAGE_PREFIX + packageName);
			}
		}
		
		Map<DotName, UnrealTypeInformer> typings = new HashMap<>();
		try (var in = getClass().getResourceAsStream("/META-INF/unreal-mappings.idx")) {
			var index = new IndexReader(in).read();
			for (var classInfo : index.getKnownClasses()) {
				var dotName = classInfo.name();
				var className = dotName.toString();
				if (packagePrefixes.stream().anyMatch(p -> className.startsWith(p))) {
					buildType(index, typings, classInfo);
				}
			}
		} catch (IOException e) {
			// should never happen
			throw new UncheckedIOException(e);
		}
		
		return typings.values().stream().collect(Collectors.toMap(t -> t.unrealTypeName, Function.identity()));
	}
	
	private UnrealTypeInformer buildType(Index classIndex, Map<DotName, UnrealTypeInformer> typings, ClassInfo classInfo) {
		// if we've already built this type, return the existing instance
		var dotName = classInfo.name();
		var informer = typings.get(dotName);
		if (informer != null) {
			return informer;
		}
		
		List<UnrealUntypedPropertyInfo> untypedProperties = new ArrayList<>();
		Map<String, Object> arrayElementTypes = new HashMap<>();
		
		if (!classInfo.isEnum()) {
			// determine the superclass so we can add its dynamic array info to this class
			// have seen classes that inherit from classes with untyped vars
			// though haven't seen subclasses that have additional untyped vars, so not sure what the behavior is in that case
			if (!classInfo.superName().toString().startsWith("java.")) {
				var superTypeInformer = buildType(classIndex, typings, classIndex.getClassByName(classInfo.superName()));
				arrayElementTypes.putAll(superTypeInformer.arrayElementTypes);
				untypedProperties.addAll(superTypeInformer.untypedProperties);
			}
			
			// iterate fields to look for untyped properties and dynamic arrays
			for (var field : classInfo.fields()) {
				var fieldType = field.type();
				
				// if property is untyped, we need lots of special logic
				var untypedPropertyAnnotation = field.annotation(UNTYPED_PROPERTY_ANNOTATION);
				if (untypedPropertyAnnotation != null) {
					Object propertyType = null;
					Object arrayElementType = null;
					Object mapKeyType = null;
					Object mapValueType = null;
					
					switch (fieldType.kind()) {
						case PARAMETERIZED_TYPE:
							String className = fieldType.name().toString();
							var params = fieldType.asParameterizedType().arguments();
							if ("java.util.List".equals(className)) {
								propertyType = UnrealDataType.arrayproperty;
								arrayElementType = determineParseType(classIndex, typings, params.get(0));
							} else if ("java.util.Map".equals(className)) {
								propertyType = Map.class;
								mapKeyType = determineParseType(classIndex, typings, params.get(0));
								mapValueType = determineParseType(classIndex, typings, params.get(1));
							} else {
								throw new IllegalArgumentException("No support for untyped property of class " + className);
							}
							break;
						case CLASS:
						case PRIMITIVE:
							propertyType = determineParseType(classIndex, typings, fieldType); 
							break;
						default:
							// should never happen
							throw new IllegalStateException("Unexpected kind " + fieldType.kind());
					}
					
					untypedProperties.add(new UnrealUntypedPropertyInfo(
							untypedPropertyAnnotation.value().asInt(), field.name(), propertyType, arrayElementType, mapKeyType, mapValueType));
					
					// even if the type is a dynamic array, we already know everything we need
					continue;
				}
				
				// at this point we are definitely looking at a typed property, so check if the Java mapping is List
				// if yes, it may be a dynamic array in Unrealscript, so determine the element type
				if (fieldType.kind() == Kind.PARAMETERIZED_TYPE && "java.util.List".equals(fieldType.name().toString())) {
					arrayElementTypes.put(
							field.name(),
							determineParseType(classIndex, typings, fieldType.asParameterizedType().arguments().get(0)));
				}
			}
			
			untypedProperties.sort((a, b) -> Integer.compare(a.position, b.position));
		}
		
		var typeNameAnnotation = classInfo.declaredAnnotation(TYPE_NAME_ANNOTATION);
		var unrealTypeName = typeNameAnnotation == null ? dotName.local() : typeNameAnnotation.value().asString();
		var isSingletonStateType = classInfo.declaredAnnotation(SINGLETON_STATE_ANNOTATION) != null;
		var isUntypedStruct = classInfo.declaredAnnotation(UNTYPED_STRUCT_ANNOTATION) != null;
		
		try {
			informer = new UnrealTypeInformer(
					unrealTypeName, Class.forName(dotName.toString()), isSingletonStateType, isUntypedStruct,
					arrayElementTypes, untypedProperties);
		} catch (ClassNotFoundException e) {
			// should never happen
			throw new RuntimeException(e);
		}
		typings.put(dotName, informer);
		
		return informer;
	}
	
	private Object determineParseType(Index classIndex, Map<DotName, UnrealTypeInformer> typings, Type elementType) {
		Object parseDataType;
		var typeAnnotation = elementType.annotation(TYPE_HINT_ANNOTATION);
		if (typeAnnotation == null) {
			// no annotation, so pick an appropriate type based on the field's type
			var elementTypeName = elementType.name().toString();
			if ("java.lang.String".equals(elementTypeName)) {
				parseDataType = UnrealDataType.strproperty;
			} else if ("int".equals(elementTypeName) || "java.lang.Integer".equals(elementTypeName)) {
				parseDataType = UnrealDataType.intproperty;
			} else if ("float".equals(elementTypeName) || "java.lang.Float".equals(elementTypeName)) {
				parseDataType = UnrealDataType.floatproperty;
			} else if ("boolean".equals(elementTypeName) || "java.lang.Boolean".equals(elementTypeName)) {
				parseDataType = UnrealDataType.boolproperty;
			} else if ("byte".equals(elementTypeName) || "java.lang.Byte".equals(elementTypeName)) {
				parseDataType = UnrealDataType.byteproperty;
			} else if ("double".equals(elementTypeName) || "java.lang.Double".equals(elementTypeName)) {
				// native vars can be double even though Unrealscript does not have a double type
				parseDataType = Double.class;
			} else {
				// struct or enum
				parseDataType = buildType(classIndex, typings, classIndex.getClassByName(elementType.name())).unrealTypeName;
			}
		} else {
			parseDataType = UnrealDataType.valueOf(typeAnnotation.value().asEnum());
			if (parseDataType == UnrealDataType.arrayproperty || parseDataType == UnrealDataType.structproperty) {
				// according to https://wiki.beyondunreal.com/Dynamic_arrays:
				// "The inner type of a dynamic array can be a class limiter or the name of any other non-array type"
				// so can't be array
				// can't be struct either because we need to know the type of the struct
				throw new IllegalArgumentException("Explicit Unreal element type cannot be array or struct");
			}
		}
		return parseDataType;
	}
	
}
