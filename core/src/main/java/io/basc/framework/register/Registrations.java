package io.basc.framework.register;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.basc.framework.limit.Limiter;
import io.basc.framework.limit.NoOpLimiter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.ConsumeProcessor;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Registrations<T extends Registration> extends AbstractRegistration implements Registry<T> {
	private final Elements<T> services;
	private final Registration master;

	public Registrations(Elements<T> services) {
		this(new NoOpLimiter(), services);
	}

	public Registrations(@NonNull Limiter limiter, Elements<T> services) {
		super(limiter);
		this.services = services == null ? Elements.empty() : services;
		this.master = Registration.EMPTY;
	}

	protected Registrations(@NonNull Registrations<T> registrations) {
		super(registrations);
		this.services = registrations.services;
		this.master = registrations.master;
	}

	private Registrations(AbstractRegistration abstractRegistration, Registration master, Elements<T> services) {
		super(abstractRegistration);
		this.master = master;
		this.services = services;
	}

	@Override
	public void reload() {
	}

	@Override
	public void unregister(Runnable runnable) throws RegistrationException {
		super.unregister(() -> {
			try {
				this.master.unregister();
			} finally {
				runnable.run();
			}
		});
	}

	@Override
	public final boolean isInvalid() {
		return isInvalid(() -> services.allMatch((e) -> e.isInvalid()));
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(() -> master.isInvalid() && checker.getAsBoolean());
	}

	@Override
	public final void unregister() throws RegistrationException {
		unregister(() -> ConsumeProcessor.consumeAll(services.reverse(), (e) -> e.unregister()));
	}

	@Override
	public <R extends Registration> Registrations<R> map(Function<? super T, ? extends R> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return new Registrations<>(this, this.master, this.services.map(mapper));
	}

	@Override
	public Registrations<T> and(Registration registration) {
		if (registration == null || registration == Registration.EMPTY) {
			return this;
		}
		return new Registrations<>(this, this.master == null ? registration : this.master.and(registration), services);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Registrations) {
			Registrations<?> other = (Registrations<?>) obj;
			return ObjectUtils.equals(this.services, other.services);
		}
		// 很多时候只是做为装饰器在使用，所以和外部数据一致也可以通过
		return ObjectUtils.equals(this.services, obj);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(services);
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(services);
	}
}
