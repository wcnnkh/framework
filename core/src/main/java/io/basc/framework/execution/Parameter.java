package io.basc.framework.execution;

import java.io.Serializable;
import java.util.function.Predicate;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Indexed;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 执行参数的定义
 */
@Getter
@ToString
public class Parameter implements Serializable, Predicate<Indexed<? extends ParameterDescriptor>> {
	private static final long serialVersionUID = 1L;
	/**
	 * 参数名称
	 */
	private String name;
	private int index = -1;
	@NonNull
	private TypeDescriptor typeDescriptor;
	private Object value;

	public Parameter(String name, @Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(value, typeDescriptor);
		Assert.requiredArgument(StringUtils.isNotEmpty(name), "name");
		this.name = name;
	}

	public Parameter(int index, @Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(value, typeDescriptor);
		Assert.requiredArgument(index >= 0, "Parameter index cannot be less than 0");
		this.index = index;
	}

	public Parameter(@Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
		this.value = value;
		this.typeDescriptor = typeDescriptor == null
				? (value == null ? TypeDescriptor.valueOf(Object.class) : TypeDescriptor.forObject(value))
				: typeDescriptor;
	}

	@Override
	public boolean test(Indexed<? extends ParameterDescriptor> indexed) {
		if (value == null && !indexed.getElement().isNullable()) {
			// 不能为空
			return false;
		}

		if (indexed.getIndex() == this.index) {
			return true;
		}

		if (StringUtils.equals(indexed.getElement().getName(), this.name)) {
			return true;
		}

		if (indexed.getElement().getTypeDescriptor().isAssignableTo(typeDescriptor)) {
			return true;
		}
		return false;
	}
}
