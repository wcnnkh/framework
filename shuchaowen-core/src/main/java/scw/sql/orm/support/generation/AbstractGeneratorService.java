package scw.sql.orm.support.generation;

import scw.aop.ProxyUtils;
import scw.core.utils.XUtils;
import scw.data.generator.SequenceId;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.sql.orm.Column;
import scw.sql.orm.ORMException;
import scw.sql.orm.enums.OperationType;
import scw.sql.orm.support.generation.annotation.CreateTime;
import scw.sql.orm.support.generation.annotation.UpdateTime;

public abstract class AbstractGeneratorService implements GeneratorService {
	protected Logger logger = LoggerUtils.getLogger(getClass());

	protected boolean isGenerator(Object bean, Column column) {
		Object v = column.getField().getGetter().get(bean);
		if (v != null) {// 已经存在值了
			if (column.getField().getSetter().getType().isPrimitive()) {
				return ((Number) v).intValue() == 0;
			} else if (v instanceof Number) {
				if (((Number) v).intValue() == 0) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void process(GeneratorContext generatorContext) throws ORMException {
		if (generatorContext.getOperationType() == OperationType.DELETE) {
			return;
		}

		if (generatorContext.getOperationType() == OperationType.SAVE
				|| generatorContext.getOperationType() == OperationType.SAVE_OR_UPDATE) {
			if (generatorContext.getOperationType() == OperationType.SAVE_OR_UPDATE
					&& !isGenerator(generatorContext.getBean(), generatorContext.getColumn())) {
				return;
			}

			scw.sql.orm.support.generation.annotation.SequenceId sequenceId = generatorContext.getColumn()
					.getAnnotatedElement().getAnnotation(scw.sql.orm.support.generation.annotation.SequenceId.class);
			if (sequenceId != null) {
				generatorContext.getColumn().set(generatorContext.getBean(), getSequenceId(generatorContext).getId());

				Class<?> clazz = ProxyUtils.getProxyFactory().getUserClass(generatorContext.getBean().getClass());
				for (String name : sequenceId.createTime()) {
					Column column = generatorContext.getObjectRelationalMapping().getColumn(clazz, name);
					if (column == null) {
						throw new NotFoundException("clazz=" + clazz + ", column=" + name);
					}

					if (!isGenerator(generatorContext.getBean(), column)) {
						continue;
					}

					column.set(generatorContext.getBean(), getCreateTime(generatorContext));
				}
				return;
			}

			CreateTime createTime = generatorContext.getColumn().getAnnotatedElement().getAnnotation(CreateTime.class);
			if (createTime != null) {
				generatorContext.getColumn().set(generatorContext.getBean(), getCreateTime(generatorContext));
				return;
			}

			scw.sql.orm.support.generation.annotation.UUID uuid = generatorContext.getColumn().getAnnotatedElement()
					.getAnnotation(scw.sql.orm.support.generation.annotation.UUID.class);
			if (uuid != null) {
				generatorContext.getColumn().set(generatorContext.getBean(), getUUID(generatorContext));
				return;
			}

			// 如果是String走uuid流程
			if (String.class == generatorContext.getColumn().getField().getSetter().getType()) {
				generatorContext.getColumn().set(generatorContext.getBean(), getUUID(generatorContext));
				return;
			}

			if (Number.class.isAssignableFrom(generatorContext.getColumn().getField().getSetter().getType())
					|| generatorContext.getColumn().getField().getSetter().getType().isPrimitive()) {
				generatorContext.getColumn().set(generatorContext.getBean(), generateNumber(generatorContext));
				return;
			}
		}

		UpdateTime updateTime = generatorContext.getColumn().getAnnotatedElement().getAnnotation(UpdateTime.class);
		if (updateTime != null) {
			generatorContext.getColumn().set(generatorContext.getBean(), getUUID(generatorContext));
			return;
		}
	}

	public final SequenceId getSequenceId(GeneratorContext generatorContext) {
		SequenceId sId = (SequenceId) generatorContext.getAttribute(SequenceId.class);
		if (sId == null) {
			sId = generateSequeueId(generatorContext);
			generatorContext.setAttribute(SequenceId.class, sId);
			generatorContext.setAttribute(CreateTime.class, sId.getTimestamp());
		}
		return sId;
	}

	public final long getCreateTime(GeneratorContext generatorContext) {
		SequenceId sequenceId = (SequenceId) generatorContext.getAttribute(SequenceId.class);
		long t;
		if (sequenceId != null) {
			t = sequenceId.getTimestamp();
		} else {
			t = System.currentTimeMillis();
			generatorContext.setAttribute(CreateTime.class, t);
		}
		return t;
	}

	public final long getUpdateTime(GeneratorContext generatorContext) {
		return getCreateTime(generatorContext);
	}

	public String getUUID(GeneratorContext generatorContext) {
		return XUtils.getUUID();
	}

	public abstract SequenceId generateSequeueId(GeneratorContext generatorContext);

	public abstract Number generateNumber(GeneratorContext generatorContext);
}
