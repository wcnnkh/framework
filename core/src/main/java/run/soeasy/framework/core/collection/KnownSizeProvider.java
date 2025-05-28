package run.soeasy.framework.core.collection;

import java.util.function.ToLongFunction;

import lombok.NonNull;

public class KnownSizeProvider<S, W extends Provider<S>> extends KnownSizeElements<S, W>
		implements ReloadableElementsWrapper<S, W> {

	public KnownSizeProvider(@NonNull W source, @NonNull ToLongFunction<? super W> statisticsSize) {
		super(source, statisticsSize);
	}

	@Override
	public void reload() {
		getSource().reload();
	}
}