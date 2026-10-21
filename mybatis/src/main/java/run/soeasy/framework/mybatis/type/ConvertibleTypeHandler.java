package run.soeasy.framework.mybatis.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;

@RequiredArgsConstructor
@Getter
@Setter
public class ConvertibleTypeHandler<S, T> extends AbstractTypeHandler<S, T> {
	@NonNull
	private final Converter converter;

	@Override
	public S toJavaValue(Class<S> valueType, @NonNull T column) {
		return converter.convert(column, valueType);
	}

	@Override
	public T toJdbcValue(Class<T> valueType, @NonNull S property) {
		return converter.convert(property, valueType);
	}
}
