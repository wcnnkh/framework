package io.basc.framework.util.spi;

import java.util.Iterator;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncludeServiceLoader<S>
		implements ServiceLoader<S>, Listener<Elements<Elements<? extends ChangeEvent<? extends S>>>> {
	@NonNull
	private final Registry<S> registry;
	@NonNull
	private final Iterable<? extends S> iterable;
	private final Reloadable reloadable;

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<S> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(Elements<Elements<? extends ChangeEvent<? extends S>>> source) {
		// TODO Auto-generated method stub

	}
}
