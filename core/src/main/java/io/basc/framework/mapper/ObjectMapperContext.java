package io.basc.framework.mapper;

import java.util.function.Predicate;
import java.util.logging.Level;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.PredicateRegistry;
import io.basc.framework.util.alias.AliasRegistry;

public class ObjectMapperContext
		implements ParentDiscover<ObjectMapperContext>, ConversionServiceAware, Predicate<Field> {
	private static Logger defLogger = LoggerFactory.getLogger(ObjectMapperContext.class);
	private AliasRegistry aliasRegistry;
	private ConversionService conversionService;
	private Logger logger;
	private Level loggerLevel;
	private String namePrefix;
	private final PredicateRegistry<Field> predicate = new PredicateRegistry<>();
	private final ObjectMapperContext parent;
	private Boolean ignoreNull;

	public ObjectMapperContext() {
		this(null);
	}

	public ObjectMapperContext(ObjectMapperContext parent) {
		this.parent = parent;
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

	@Override
	public boolean test(Field t) {
		if (parent != null && !parent.test(t)) {
			return false;
		}
		return predicate.test(t);
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

	public void setIgnoreNull(Boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setLoggerLevel(Level loggerLevel) {
		this.loggerLevel = loggerLevel;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}
}
