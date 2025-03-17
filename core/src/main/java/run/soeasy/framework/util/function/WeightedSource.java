package run.soeasy.framework.util.function;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class WeightedSource<W> extends Wrapped<W> implements Weighted {
	private final int weight;

	public WeightedSource(@NonNull W source, int weight) {
		super(source);
		this.weight = weight;
	}
}
