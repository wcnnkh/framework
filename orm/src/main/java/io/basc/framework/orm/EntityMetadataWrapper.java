package io.basc.framework.orm;

import io.basc.framework.util.Wrapper;

public class EntityMetadataWrapper<W extends EntityMetadata> extends Wrapper<W> implements EntityMetadata {

	public EntityMetadataWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public String getCharsetName() {
		return wrappedTarget.getCharsetName();
	}

	@Override
	public String getComment() {
		return wrappedTarget.getComment();
	}
}
