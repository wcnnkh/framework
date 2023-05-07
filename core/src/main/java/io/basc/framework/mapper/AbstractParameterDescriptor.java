package io.basc.framework.mapper;

public abstract class AbstractParameterDescriptor implements ParameterDescriptor {
	private volatile Boolean nullable;

	@Override
	public boolean isNullable() {
		if (nullable == null) {
			synchronized (this) {
				if (nullable == null) {
					nullable = ParameterDescriptor.super.isNullable();
				}
			}
		}
		return nullable;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

}
