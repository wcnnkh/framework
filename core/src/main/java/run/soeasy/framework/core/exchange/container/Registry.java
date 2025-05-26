package run.soeasy.framework.core.exchange.container;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.ElementsWrapper;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<E> extends Elements<E> {
	public static interface RegistryWrapper<E, W extends Registry<E>> extends Registry<E>, ElementsWrapper<E, W> {
		@Override
		default Registration registers(Elements<? extends E> elements) throws RegistrationException {
			return getSource().registers(elements);
		}

		@Override
		default Registration register(E element) throws RegistrationException {
			return getSource().register(element);
		}
	}

	@Data
	public static class MappedRegistry<S, T, W extends Registry<S>>
			implements Registry<T>, ElementsWrapper<T, Elements<T>> {
		@NonNull
		private final W registry;
		@NonNull
		private final Codec<T, S> codec;

		@Override
		public Registration register(T element) throws RegistrationException {
			S target = codec.encode(element);
			return registry.register(target);
		}

		@Override
		public Elements<T> getSource() {
			return registry.map(codec::decode);
		}

		@Override
		public Registration registers(Elements<? extends T> elements) throws RegistrationException {
			Elements<S> target = codec.encodeAll(elements);
			return registry.registers(target);
		}
	}

	default <R> Registry<R> map(@NonNull Codec<R, E> codec) {
		return new MappedRegistry<>(this, codec);
	}

	/**
	 * 注册一个
	 * 
	 * @param element
	 * @return
	 * @throws RegistrationException
	 */
	default Registration register(E element) throws RegistrationException {
		return registers(Elements.singleton(element));
	}

	/**
	 * 批量注册
	 * 
	 * @param elements
	 * @return
	 * @throws RegistrationException
	 */
	Registration registers(@NonNull Elements<? extends E> elements) throws RegistrationException;
}
