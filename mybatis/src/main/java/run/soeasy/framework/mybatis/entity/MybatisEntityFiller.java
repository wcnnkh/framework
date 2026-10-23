package run.soeasy.framework.mybatis.entity;

import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;

import lombok.NonNull;
import run.soeasy.framework.beans.BeanUtils;
import run.soeasy.framework.core.mapping.Mapper;
import run.soeasy.framework.core.mapping.MappingContext;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.mapping.property.PropertyMappingFilter;

public interface MybatisEntityFiller {
	MybatisEntity getFillEntity(SqlCommandType sqlCommandType);

	default boolean isForceUpdate(SqlCommandType sqlCommandType, MybatisEntity sourceEntity,
			MybatisEntity targetEntity) {
		return sqlCommandType == SqlCommandType.UPDATE;
	}

	default void fillArgs(@NonNull MappedStatement mappedStatement, @NonNull Stream<Object> args, Logger logger) {
		fillArgs(mappedStatement.getSqlCommandType(), args, logger);
	}

	default void fill(@NonNull SqlCommandType sqlCommandType, MybatisEntity sourceEntity, Object target,
			Logger logger) {
		if (target instanceof ParamMap) {
			ParamMap<?> paramMap = (ParamMap<?>) target;
			for (Object value : paramMap.values()) {
				fill(sqlCommandType, sourceEntity, value, logger);
			}
		} else if (target instanceof MybatisEntity) {
			MybatisEntity targetEntity = (MybatisEntity) target;
			boolean forceUpdate = isForceUpdate(sqlCommandType, sourceEntity, targetEntity);
			BeanUtils.copyProperties(sourceEntity, targetEntity, new PropertyMappingFilter() {

				@Override
				public boolean doMapping(
						@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> sourceContext,
						@NonNull MappingContext<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> targetContext,
						@NonNull Mapper<String, PropertyAccessor, PropertyMapping<PropertyAccessor>> mapper) {
					if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
						if (!forceUpdate) {
							Object targetValue = targetContext.getKeyValue().getValue().get();
							if (targetValue != null) {
								return false;
							}
						}

						Object sourceValue = sourceContext.getKeyValue().getValue().get();
						if (sourceValue == null) {
							return false;
						}
						if (logger != null && logger.isDebugEnabled()) {
							logger.debug("The data filled with {}.{} is: {}", target.getClass().getName(),
									targetContext.getKeyValue().getKey(), sourceValue);
						}
					}
					return mapper.doMapping(sourceContext, targetContext);
				}
			});
		}
	}

	default void fillArgs(@NonNull SqlCommandType sqlCommandType, @NonNull Stream<Object> args, Logger logger) {
		MybatisEntity fillEntity = getFillEntity(sqlCommandType);
		if (fillEntity == null) {
			return;
		}

		Iterator<Object> iterator = args.iterator();
		while (iterator.hasNext()) {
			Object arg = iterator.next();
			fill(sqlCommandType, fillEntity, arg, logger);
		}
	}
}
