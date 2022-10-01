package io.basc.framework.mapper;

import java.util.function.Predicate;
import java.util.logging.Level;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.StringStrategy;
import io.basc.framework.util.alias.AliasRegistry;
import io.basc.framework.util.attribute.SimpleAttributes;

public class ObjectMapperContext extends SimpleAttributes<String, Object>
		implements ParentDiscover<ObjectMapperContext>, ConversionServiceAware {
	private static final int DEFAULT_ENTITY_NESTING_MAXIUM_DEPTH = Integer
			.getInteger("io.basc.framework.mapper.ObjectMapperContext.DEFAULT_ENTITY_NESTING_MAXIUM_DEPTH", 5);

	private static Logger defLogger = LoggerFactory.getLogger(ObjectMapperContext.class);
	private AliasRegistry aliasRegistry;
	private ConversionService conversionService;
	private Predicate<Field> filter;
	private Logger logger;
	private Level loggerLevel;
	private String nameConnector;
	private Boolean nameNesting;
	private String namePrefix;
	private final ObjectMapperContext parent;
	private Boolean ignoreNull;
	private final StringStrategy<Boolean> entityTypeMatcher;
	private final StringStrategy<Boolean> ignoreAnnotationNameMatcher;
	private final StringStrategy<Boolean> ignoreNameMatcher;
	private Integer entityNestingMaxiumDepth;

	public ObjectMapperContext() {
		this(null);
	}

	public ObjectMapperContext(ObjectMapperContext parent) {
		this.parent = parent;
		this.entityTypeMatcher = new StringStrategy<Boolean>(AntPathMatcher.POINT_PATH_MATCHER,
				parent == null ? null : parent.entityTypeMatcher);
		this.ignoreAnnotationNameMatcher = new StringStrategy<Boolean>(AntPathMatcher.POINT_PATH_MATCHER,
				parent == null ? null : parent.ignoreAnnotationNameMatcher);
		this.ignoreNameMatcher = new StringStrategy<Boolean>(AntPathMatcher.POINT_PATH_MATCHER,
				parent == null ? null : parent.ignoreNameMatcher);
	}

	public AliasRegistry getAliasRegistry() {
		if (this.aliasRegistry != null) {
			return this.aliasRegistry;
		}

		if (this.parent != null) {
			return this.parent.getAliasRegistry();
		}
		return null;
	}

	public ConversionService getConversionService() {
		if (this.conversionService != null) {
			return this.conversionService;
		}

		if (this.parent != null) {
			return this.parent.getConversionService();
		}

		return Sys.getEnv().getConversionService();
	}

	public Predicate<Field> getFilter() {
		if (this.filter == null) {
			if (this.parent == null) {
				return null;
			} else {
				return this.parent.getFilter();
			}
		} else {
			if (this.parent == null) {
				return this.filter;
			} else {
				Predicate<Field> parentFilter = this.parent.getFilter();
				if (parentFilter == null) {
					return this.filter;
				}

				return parentFilter.and(this.filter);
			}
		}
	}

	public void addFilter(Predicate<Field> filter) {
		if (filter == null) {
			return;
		}

		if (this.filter == null) {
			this.filter = filter;
		} else {
			this.filter.and(filter);
		}
	}

	public Logger getLogger() {
		if (this.logger != null) {
			return this.logger;
		}

		if (this.parent != null) {
			return this.parent.getLogger();
		}

		return defLogger;
	}

	public Level getLoggerLevel() {
		if (this.loggerLevel != null) {
			return this.loggerLevel;
		}

		if (this.parent != null) {
			return this.parent.getLoggerLevel();
		}

		return io.basc.framework.logger.Levels.DEBUG.getValue();
	}

	public String getNameConnector() {
		if (this.nameConnector != null) {
			return this.nameConnector;
		}

		if (this.parent != null) {
			return this.parent.getNameConnector();
		}

		return ".";
	}

	public String getNamePrefix() {
		if (this.namePrefix != null) {
			return this.namePrefix;
		}

		if (this.parent != null) {
			return this.parent.getNamePrefix();
		}

		return null;
	}

	@Override
	public ObjectMapperContext getParent() {
		return parent;
	}

	public boolean isNameNesting() {
		if (this.nameNesting != null) {
			return this.nameNesting;
		}

		if (this.parent != null) {
			return this.parent.isNameNesting();
		}

		return true;
	}

	public boolean isIgnoreNull() {
		if (this.ignoreNull != null) {
			return this.ignoreNull;
		}

		if (this.parent != null) {
			return this.parent.isIgnoreNull();
		}
		return true;
	}

	public void setAliasRegistry(AliasRegistry aliasRegistry) {
		this.aliasRegistry = aliasRegistry;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setFilter(Predicate<Field> filter) {
		this.filter = filter;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setLoggerLevel(Level loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	public void setNameConnector(String nameConnector) {
		this.nameConnector = nameConnector;
	}

	public void setNameNesting(Boolean nameNesting) {
		this.nameNesting = nameNesting;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public void setIgnoreNull(Boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public StringStrategy<Boolean> getEntityTypeMatcher() {
		return entityTypeMatcher;
	}

	public StringStrategy<Boolean> getIgnoreAnnotationNameMatcher() {
		return ignoreAnnotationNameMatcher;
	}

	public StringStrategy<Boolean> getIgnoreNameMatcher() {
		return ignoreNameMatcher;
	}

	public int getEntityNestingMaxiumDepth() {
		if (this.entityNestingMaxiumDepth == null) {
			if (this.parent != null) {
				return this.parent.getEntityNestingMaxiumDepth();
			}
		}
		return entityNestingMaxiumDepth == null ? DEFAULT_ENTITY_NESTING_MAXIUM_DEPTH : entityNestingMaxiumDepth;
	}

	public void setEntityNestingMaxiumDepth(Integer entityNestingMaxiumDepth) {
		this.entityNestingMaxiumDepth = entityNestingMaxiumDepth;
	}
}
