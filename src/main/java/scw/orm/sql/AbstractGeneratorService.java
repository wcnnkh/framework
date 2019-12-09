package scw.orm.sql;

import scw.core.utils.XUtils;
import scw.id.SequenceId;
import scw.lang.NotSupportException;
import scw.orm.ORMException;
import scw.orm.sql.annotation.CreateTime;
import scw.orm.sql.annotation.UUID;
import scw.orm.sql.annotation.UpdateTime;
import scw.orm.sql.enums.OperationType;

public abstract class AbstractGeneratorService implements GeneratorService {

	public void process(GeneratorContext generatorContext) throws ORMException {
		if (generatorContext.getOperationType() == OperationType.DELETE) {
			return;
		}

		if (generatorContext.getOperationType() != OperationType.UPDATE) {
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
		}

		UpdateTime updateTime = generatorContext.getMappingContext().getColumn().getAnnotation(UpdateTime.class);
		if (updateTime != null) {
			generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
					getUUID(generatorContext));
			return;
		}

		// 如果是String走uuid流程
		if (String.class == generatorContext.getMappingContext().getColumn().getField().getType()) {
			generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
					getUUID(generatorContext));
			return;
		}

		if (Number.class.isAssignableFrom(generatorContext.getMappingContext().getColumn().getField().getType())) {
			generatorContext.getSqlMapper().setter(generatorContext.getMappingContext(), generatorContext.getBean(),
					generateNumber(generatorContext));
		}
		throw new NotSupportException(
				"不支持的生成方式clazz=" + generatorContext.getMappingContext().getDeclaringClass().getName() + ", field="
						+ generatorContext.getMappingContext().getColumn().getField().getName());
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
