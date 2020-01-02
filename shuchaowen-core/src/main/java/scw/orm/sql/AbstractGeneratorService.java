package scw.orm.sql;

import scw.core.utils.XUtils;
import scw.id.SequenceId;
import scw.orm.ORMException;
import scw.orm.sql.annotation.CreateTime;
import scw.orm.sql.annotation.UUID;
import scw.orm.sql.annotation.UpdateTime;
import scw.orm.sql.enums.OperationType;
import scw.orm.sql.support.GeneratorContext;

public abstract class AbstractGeneratorService implements GeneratorService {

	protected boolean isGenerator(GeneratorContext generatorContext) {
		Object v = generatorContext.getMappingContext().getColumn().get(generatorContext.getBean());
		if (v != null) {// 已经存在值了
			if (generatorContext.getMappingContext().getColumn().getType().isPrimitive()) {
				return (Integer) v == 0;
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
			if (generatorContext.getOperationType() == OperationType.SAVE_OR_UPDATE && !isGenerator(generatorContext)) {
				return;
			}

			scw.orm.sql.annotation.SequenceId sequenceId = generatorContext.getMappingContext().getColumn()
					.getAnnotation(scw.orm.sql.annotation.SequenceId.class);
			if (sequenceId != null) {
				generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
						getSequenceId(generatorContext).getId());
				return;
			}

			CreateTime createTime = generatorContext.getMappingContext().getColumn().getAnnotation(CreateTime.class);
			if (createTime != null) {
				generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
						getCreateTime(generatorContext));
				return;
			}

			UUID uuid = generatorContext.getMappingContext().getColumn().getAnnotation(UUID.class);
			if (uuid != null) {
				generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
						getUUID(generatorContext));
				return;
			}

			// 如果是String走uuid流程
			if (String.class == generatorContext.getMappingContext().getColumn().getType()) {
				generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
						getUUID(generatorContext));
				return;
			}

			if (Number.class.isAssignableFrom(generatorContext.getMappingContext().getColumn().getType())
					|| generatorContext.getMappingContext().getColumn().getType().isPrimitive()) {
				generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
						generateNumber(generatorContext));
				return;
			}
		}

		UpdateTime updateTime = generatorContext.getMappingContext().getColumn().getAnnotation(UpdateTime.class);
		if (updateTime != null) {
			generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
					getUUID(generatorContext));
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
