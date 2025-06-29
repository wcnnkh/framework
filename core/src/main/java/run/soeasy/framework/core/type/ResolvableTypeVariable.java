package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;

import lombok.Getter;
import lombok.NonNull;

/**
 * 可解析的类型参数
 * 
 * @author soeasy.run
 *
 */
@Getter
public class ResolvableTypeVariable extends AbstractResolvableType<TypeVariable<?>>
		implements ResolvableTypeWrapper<ResolvableType> {

	public ResolvableTypeVariable(@NonNull TypeVariable<?> type, TypeVariableResolver typeVariableResolver) {
		super(type, typeVariableResolver);
	}

	public ResolvableType[] getBounds() {
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getBounds());
	}

	@Override
	public ResolvableType getSource() {
		ResolvableType resolvableType = getTypeVariableResolver() == null ? null
				: getTypeVariableResolver().resolveTypeVariable(getType());
		return resolvableType == null ? NONE : resolvableType;
	}

	@Override
	public String getTypeName() {
		ResolvableType resolved = getSource();
		if (resolved == null || resolved == NONE) {
			return getType().getTypeName();
		}
		return resolved.getTypeName();
	}

	@Override
	public final ResolvableType[] getLowerBounds() {
		return getBounds();
	}

	@Override
	public final ResolvableType[] getUpperBounds() {
		return getBounds();
	}
}
