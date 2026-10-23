package run.soeasy.framework.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 一个智能的、由 Spring 驱动的适配器映射容器。
 * 
 * <p>
 * 该类继承自 {@link ConcurrentHashMap}，旨在自动发现并注册 Spring 容器中特定类型的适配器 Bean。 它通过实现
 * {@link BeanFactoryAware} 和 {@link InitializingBean} 接口，在 Spring 上下文初始化时，
 * 自动查找所有匹配泛型类型 {@code V} 的 Bean，并使用提供的 {@link Function} 将这些 Bean 映射到相应的键
 * {@code K}。
 * 
 * <p>
 * 典型用法是作为一个单例 Bean 存在，用于集中管理一组具有共同接口或父类的策略类或适配器类。 例如，你可能有多个支付适配器（如
 * AliPayAdapter, WeChatPayAdapter），它们都实现了 {@code PayAdapter} 接口。 通过将
 * {@code AdapterMap} 的泛型指定为 {@code <String, PayAdapter>}，并提供一个从
 * {@code PayAdapter} 中提取或生成支付类型（如 "ALIPAY", "WECHAT"）作为键的
 * {@link Function}，该类就能自动将所有支付适配器注册到一个 Map 中， 方便在业务代码中根据支付类型快速获取对应的适配器。
 * 
 * @param <K> 映射中键的类型
 * @param <V> 映射中值的类型，通常是一个特定的接口或抽象类，所有适配器 Bean 都应实现或继承它
 * 
 * @author soeasy.run
 * @see BeanFactoryAware
 * @see InitializingBean
 * @see ConcurrentHashMap
 */
@Getter
@Setter
@RequiredArgsConstructor
public class AdapterMap<K, V> extends ConcurrentHashMap<K, V> implements BeanFactoryAware, InitializingBean {
	private static final long serialVersionUID = 1L;

	/**
	 * Spring 的 Bean 工厂，用于获取所有符合条件的适配器 Bean。
	 */
	private transient BeanFactory beanFactory;

	/**
	 * 用于解析适配器类型 {@code V} 的 {@link ResolvableType} 对象。 如果未手动设置，它将在首次访问时通过反射自动计算。
	 */
	private ResolvableType adapterType;

	/**
	 * 一个函数，用于从适配器 Bean ({@code V}) 中生成对应的键 ({@code K})。 这个函数在注册每个适配器时被调用。
	 */
	@NonNull
	private transient final Function<V, K> keyGenerator;

	/**
	 * 获取用于查找适配器 Bean 的 {@link ResolvableType}。
	 * 
	 * <p>
	 * 如果 {@code adapterType} 字段为 {@code null}，该方法会通过反射计算当前实例的泛型超类
	 * {@code AdapterMap<K, V>} 中第二个泛型参数 {@code V} 的具体类型。
	 * 
	 * @return 解析后的适配器类型
	 */
	public ResolvableType getAdapterType() {
		if (adapterType == null) {
			this.adapterType = ResolvableType.forClass(getClass()).as(AdapterMap.class).getGeneric(1);
		}
		return adapterType;
	}

	/**
	 * 在 Bean 初始化完成后，自动执行适配器的发现和注册逻辑。
	 * 
	 * <p>
	 * 该方法会：
	 * <ol>
	 * <li>获取 {@link #adapterType}。
	 * <li>使用 {@link BeanFactory} 获取所有类型为 {@code adapterType} 的 Bean。
	 * <li>遍历这些 Bean，使用 {@link #keyGenerator} 为每个 Bean 生成一个键。
	 * <li>将键和 Bean 本身存入当前的 Map 中。
	 * </ol>
	 * 
	 * @throws Exception 如果在查找或注册 Bean 的过程中发生任何错误
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (keyGenerator == null) {
			return;
		}

		BeanFactory beanFactory = getBeanFactory();
		if (beanFactory == null) {
			return;
		}
		ResolvableType adapterType = getAdapterType();
		ObjectProvider<Object> objectProvider = beanFactory.getBeanProvider(adapterType);
		for (Object adapter : objectProvider) {
			V value = (V) adapter;
			K key = keyGenerator.apply(value);
			put(key, value);
		}
	}
}