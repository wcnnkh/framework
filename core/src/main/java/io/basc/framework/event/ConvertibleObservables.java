package io.basc.framework.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.basc.framework.convert.Converter;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Combiner;

public class ConvertibleObservables<S, T> extends AbstractObservable<T> implements AutoCloseable {
	private final Set<Observable<S>> observables;
	private final Combiner<S> combiner;
	private final Converter<S, T> converter;
	private final List<EventRegistration> registrations;

	public ConvertibleObservables(Converter<S, T> converter, Combiner<S> combiner) {
		this.converter = converter;
		this.combiner = combiner;
		this.observables = CollectionFactory.createSet(isConcurrent());
		this.registrations = CollectionFactory.createArrayList(isConcurrent());
	}

	public boolean combine(Observable<S> observable) {
		if (this.observables.add(observable)) {
			publishEvent(new ChangeEvent<T>(EventType.UPDATE, forceGet()));
			EventRegistration registration = observable.registerListener((event) -> publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet())));
			registrations.add(registration);
			return true;
		}
		return false;
	}

	public List<Observable<S>> getObservables() {
		if (CollectionUtils.isEmpty(observables)) {
			return Collections.emptyList();
		}

		List<Observable<S>> observables = new ArrayList<Observable<S>>(this.observables.size());
		for (Observable<S> registion : this.observables) {
			observables.add(registion);
		}

		// 排序,优先级降序排列，那么一般情况下进行合并后面的会覆盖前面的
		observables.sort(OrderComparator.INSTANCE);
		return observables;
	}

	@Override
	protected T forceGet() {
		List<Observable<S>> observables = getObservables();
		List<S> list;
		if (CollectionUtils.isEmpty(observables)) {
			list = Collections.emptyList();
		} else {
			list = new ArrayList<S>(observables.size());
			for (Observable<S> observable : observables) {
				list.add(observable.get());
			}
		}
		S value = combiner.combine(list);
		return converter.convert(value);
	}

	@Override
	public void close() {
		for (EventRegistration registration : registrations) {
			registration.unregister();
		}
	}
}
