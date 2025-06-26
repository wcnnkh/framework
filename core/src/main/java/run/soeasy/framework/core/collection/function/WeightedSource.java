package run.soeasy.framework.core.collection.function;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;

@Getter
public class WeightedSource<W> extends Wrapped<W> implements Weighted {
	private final int weight;

	public WeightedSource(@NonNull W source, int weight) {
		super(source);
		this.weight = weight;
	}
}
