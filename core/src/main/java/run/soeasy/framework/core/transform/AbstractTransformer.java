package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;

@Getter
@Setter
public abstract class AbstractTransformer implements Transformer, TransformerAware {
	@NonNull
	private Transformer transformer = Transformer.ignore();

	@Override
	public abstract boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor);
}
