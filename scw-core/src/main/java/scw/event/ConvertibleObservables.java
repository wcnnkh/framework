package scw.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import scw.convert.Converter;
import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.util.CollectionFactory;
import scw.util.Combiner;

public class ConvertibleObservables<S, T> extends AbstractObservable<T> {
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
			EventRegistration registration = observable.registerListener(new EventListener<ChangeEvent<S>>() {

				@Override
				public void onEvent(ChangeEvent<S> event) {
					publishEvent(new ChangeEvent<T>(event.getEventType(), forceGet()));
				}
			});
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
	protected void finalize() throws Throwable {
		for (EventRegistration registration : registrations) {
			registration.unregister();
		}
		super.finalize();
	}
}
