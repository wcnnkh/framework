package io.basc.framework.mapper;

/**
 * 相同的字段名就视为同一个getter
 * 
 * @author wcnnkh
 *
 */
public abstract class AbstractGetter extends AbstractParameterDescriptor implements Getter {

	@Override
	public int hashCode() {
		return getName().hashCode();
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
			return getName() == getter.getName();
		}
		return false;
	}

	@Override
	public String toString() {
		return getTypeDescriptor().getResolvableType().toString() + " " + getName() + "()";
	}
}
