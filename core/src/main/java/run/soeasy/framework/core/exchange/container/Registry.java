package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<E> extends Elements<E> {
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
