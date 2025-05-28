package run.soeasy.framework.core.exchange.container;

import java.util.function.Function;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

@Data
public class MappedPayloadRegistration<S, T, W extends PayloadRegistration<S>>
		implements PayloadRegistration<T>, RegistrationWrapper<W> {
	@NonNull
	private final W source;
	@NonNull
	private final Function<? super S, ? extends T> mapper;

	@Override
	public T getPayload() {
		S payload = source.getPayload();
		return mapper.apply(payload);
	}
}