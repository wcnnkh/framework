package io.basc.framework.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.alias.AliasRegistry;

public abstract class AbstractObjectMapper<S, E extends Throwable> extends SimpleReverseMapperFactory<S, E>
		implements ObjectMapper<S, E>, ConversionServiceAware {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<Class<?>, Structure<? extends Field>> map = new ConcurrentHashMap<>();
	private String namePrefix;
	private String nameConnector = ".";
	private AliasRegistry aliasRegistry;
	private boolean transformSuperclass = true;
	private ConversionService conversionService;
	private Level loggerLevel = io.basc.framework.logger.Levels.DEBUG.getValue();
	private Predicate<Field> fieldFilter;
	private Field parentField;

	@Override
	public final ConversionService getConversionService() {
		if (this.conversionService != null) {
			return this.conversionService;
		}
		return ObjectMapper.super.getConversionService();
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public final boolean isTransformSuperclass() {
		return transformSuperclass;
	}

	public void setTransformSuperclass(boolean transformSuperclass) {
		this.transformSuperclass = transformSuperclass;
	}

	@Override
	public Structure<? extends Field> getStructure(Class<?> entityClass) {
		Structure<? extends Field> structure = map.get(entityClass);
		if (structure == null) {
			structure = ObjectMapper.super.getStructure(entityClass);
		}

		if (structure.getParent() == null && this.parentField != null) {
			structure = structure.setParentField(this.parentField);
		}

		if (transformSuperclass) {
			structure = structure.all();
		}
		return structure.shared();
	}

	public final AliasRegistry getAliasRegistry() {
		return aliasRegistry;
	}

	public void setAliasRegistry(AliasRegistry aliasRegistry) {
		this.aliasRegistry = aliasRegistry;
	}

	public final Level getLoggerLevel() {
		return loggerLevel;
	}

	public void setLoggerLevel(Level loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	public final Predicate<Field> getFieldFilter() {
		return fieldFilter;
	}

	public void setFieldFilter(Predicate<Field> fieldFilter) {
		this.fieldFilter = fieldFilter;
	}

	public final Field getParentField() {
		return parentField;
	}

	public void setParentField(Field parentField) {
		this.parentField = parentField;
	}

	public final Logger getLogger() {
		return logger;
	}

	@Override
	public void registerStructure(Class<?> entityClass, Structure<? extends Field> structure) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (structure == null) {
			map.remove(entityClass);
		} else {
			map.put(entityClass, structure);
		}
	}

	private Collection<String> getUseSetterNames(AliasRegistry aliasRegistry, Class<?> entityClass, Field field) {
		List<String> useNames = new ArrayList<String>(8);
		Collection<String> names = field.getAliasNames();
		for (String name : names) {
			useNames.add(name);
			if (aliasRegistry != null) {
				for (String alias : aliasRegistry.getAliases(name)) {
					useNames.add(alias);
				}
			}
		}
		return useNames;
	}

	private String toUseName(String parentName, String name) {
		StringBuilder nameAppend = new StringBuilder(32);
		if (namePrefix != null) {
			nameAppend.append(namePrefix);
		}

		if (parentName != null) {
			nameAppend.append(parentName);
		}

		if (nameAppend.length() != 0) {
			nameAppend.append(nameConnector);
		}

		nameAppend.append(name);
		return nameAppend.toString();
	}

	public abstract Collection<String> getAliasNames(Class<?> entityClass);

	private void appendNames(Class<?> entityClass, @Nullable AliasRegistry aliasRegistry, Collection<String> names,
			String parentName, Field field) {
		Field parent = field.getParent();
		if (parent == null) {
			// 最顶层的字段
			Collection<String> aliasNames = field.getAliasNames();
			for (String name : aliasNames) {
				names.add(toUseName(parentName, name));
				if (aliasRegistry != null) {
					for (String alias : aliasRegistry.getAliases(name)) {
						names.add(toUseName(parentName, alias));
					}
				}
			}

			// 该字段的声明类
			for (String entityName : getAliasNames(field.getSetter().getDeclaringClass())) {
				for (String alias : aliasNames) {
					names.add(toUseName(parentName, entityName + nameConnector + alias));
					if (aliasRegistry != null) {
						for (String aliasName : aliasRegistry.getAliases(alias)) {
							names.add(toUseName(parentName, entityName + nameConnector + aliasName));
						}
					}
				}
			}
		} else {
			for (String name : getUseSetterNames(aliasRegistry, entityClass, parent)) {
				appendNames(entityClass, aliasRegistry, names,
						parentName == null ? (name + nameConnector) : (name + nameConnector + parentName),
						field.getParent());
			}
		}
	}

	public Collection<String> getAliasNames(Class<?> entityClass, Field field) {
		Set<String> names = new LinkedHashSet<String>(8);
		appendNames(entityClass, getAliasRegistry(), names, null, field);
		return names;
	}
}
