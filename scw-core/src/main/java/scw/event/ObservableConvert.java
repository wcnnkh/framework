package scw.event;

import scw.core.Converter;

public class ObservableConvert<O, T> extends AbstractObservableConvert<O, T> {
	private final Converter<O, T> converter;

	public ObservableConvert(Observable<O> observable, Converter<O, T> converter) {
		super(observable);
		this.converter = converter;
	}

	public T convert(O o) {
		return converter.convert(o);
	}
}
