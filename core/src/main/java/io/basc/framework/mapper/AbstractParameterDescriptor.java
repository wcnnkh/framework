package io.basc.framework.mapper;

/**
 * 主要是为了重写hash和equals
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractParameterDescriptor implements ParameterDescriptor {
	@Override
	public int hashCode() {
		return getTypeDescriptor().getType().hashCode() + getName().hashCode();
	}

	/**
	 * 名称为类型(不比较泛型)相同就认为是同一个
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Getter) {
			Getter getter = (Getter) obj;
			return getTypeDescriptor().getType() == getter.getTypeDescriptor().getType()
					&& getName() == getter.getName();
		}
		return false;
	}
}
