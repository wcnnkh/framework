package scw.sql.orm.support.generation;

import scw.aop.ProxyUtils;
import scw.core.utils.XUtils;
import scw.data.generator.SequenceId;
import scw.lang.NotFoundException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.MapperUtils;
import scw.sql.orm.Column;
import scw.sql.orm.ORMException;
import scw.sql.orm.enums.OperationType;
import scw.sql.orm.support.generation.annotation.CreateTime;
import scw.sql.orm.support.generation.annotation.UpdateTime;
import scw.util.Accept;

public abstract class AbstractGeneratorService implements GeneratorService {
	protected Logger logger = LoggerUtils.getLogger(getClass());
	private final TemporaryVariable temporaryVariable = new TemporaryVariable();

	public TemporaryVariable getTemporaryVariable() {
		return temporaryVariable;
	}

	protected boolean isGenerator(Object bean, Column column) {
		return !MapperUtils.isExistValue(column.getField(), bean);
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
			generatorContext.getColumn().set(generatorContext.getBean(), getUpdateTime(generatorContext));
			return;
		}
	}

	public final SequenceId getSequenceId(GeneratorContext generatorContext) {
		SequenceId sId = (SequenceId) generatorContext.getAttribute(SequenceId.class);
		if (sId == null) {
			sId = generateSequeueId(generatorContext);
			getTemporaryVariable().setSequeueId(generatorContext, sId);
			getTemporaryVariable().setCreateTime(generatorContext, sId.getTimestamp());
		}
		return sId;
	}

	public final long getCreateTime(GeneratorContext generatorContext) {
		SequenceId sequenceId = getTemporaryVariable().getSequenceId(generatorContext);
		long t;
		if (sequenceId != null) {
			t = sequenceId.getTimestamp();
		} else {
			t = System.currentTimeMillis();
			getTemporaryVariable().setCreateTime(generatorContext, t);
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

	static class TemporaryVariable {

		public SequenceId getSequenceId(GeneratorContext generatorContext) {
			return (SequenceId) generatorContext.getAttribute(SequenceId.class);
		}

		public void setSequeueId(GeneratorContext generatorContext, SequenceId sequenceId) {
			generatorContext.setAttribute(SequenceId.class, sequenceId);
		}

		public Long getCreateTime(GeneratorContext generatorContext) {
			Long createTime = (Long) generatorContext.getAttribute(CreateTime.class);
			if (createTime == null) {
				Column createTimeColumn = generatorContext.getObjectRelationalMapping()
						.findColumn(generatorContext.getBean().getClass(), new Accept<Column>() {

							public boolean accept(Column e) {
								return e.getAnnotatedElement().getAnnotation(CreateTime.class) != null;
							}
						});

				if (createTimeColumn != null) {
					createTime = (Long) createTimeColumn.getField().getGetter().get(generatorContext.getBean());
					if (createTime != null && createTime.longValue() != 0) {
						setCreateTime(generatorContext, createTime);
						return createTime;
					}
				}
			}
			return createTime;
		}

		public void setCreateTime(GeneratorContext generatorContext, long createTime) {
			generatorContext.setAttribute(CreateTime.class, createTime);
		}
	}
}
