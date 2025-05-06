package run.soeasy.framework.core.convert;

import java.util.function.BiFunction;

import lombok.NonNull;

public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {
	public static interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
			extends TypedValueAccessor, TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {
		@Override
		default TypedValue getAsValue(
				@NonNull BiFunction<? super TypedValue, ? super TargetDescriptor, ? extends Object> mapper) {
			return getSource().getAsValue(mapper);
		}
	}

	@Override
	default TypedValue getAsValue(
			@NonNull BiFunction<? super TypedValue, ? super TargetDescriptor, ? extends Object> mapper) {
		ConvertingValue<AccessibleDescriptor> converting = new ConvertingValue<AccessibleDescriptor>(this);
		converting.setValue(this);
		converting.setMapper(mapper);
		return converting;
	}
}
