package run.soeasy.framework.core.mapping.property;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.exchange.MapContainer;

/**
 * 对象模板注册表，继承自{@link MapContainer}并实现{@link ObjectTemplateFactory}，
 * 用于缓存和管理对象类与属性模板之间的映射关系，支持线程安全的模板注册和获取操作。
 * <p>
 * 该类采用读写锁机制实现并发控制，在多线程环境下保证数据一致性和高性能访问。
 * 当请求的模板不存在时，可通过配置的{@link ObjectTemplateFactory}动态生成并注册模板。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>模板缓存：基于Map结构缓存已注册的对象模板，提高重复访问效率</li>
 * <li>线程安全：使用（{@link ConcurrentHashMap}）保证并发环境下的数据一致性</li>
 * </ul>
 *
 * <p>
 * <b>泛型说明：</b>
 * <ul>
 * <li>{@code E}：属性类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>对象映射框架：缓存类与属性映射关系，避免重复反射解析</li>
 * <li>多线程环境：在并发场景下安全地管理和获取对象模板</li>
 * <li>动态扩展：支持运行时注册新的对象模板</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ObjectTemplateFactory
 * @see PropertyMapping
 * @see MapContainer
 */
@Getter
@Setter
public class ObjectTemplateRegistry<E extends Property>
		extends MapContainer<Class<?>, PropertyMapping<E>, ConcurrentHashMap<Class<?>, PropertyMapping<E>>>
		implements ObjectTemplateFactory<E> {

	/** 用于动态生成对象模板的工厂，可为null */
	private volatile ObjectTemplateFactory<E> objectTemplateFactory;

	/**
	 * 构造对象模板注册表
	 * <p>
	 * 初始化读写锁以支持并发操作。
	 */
	public ObjectTemplateRegistry() {
		super(new ConcurrentHashMap<>());
	}

	/**
	 * 获取指定对象类的属性模板
	 * <p>
	 * 该方法首先检查缓存中是否存在对应的模板：
	 * <ol>
	 * <li>若存在则直接返回</li>
	 * <li>若不存在且配置了工厂，则使用双重检查锁定机制创建并注册模板</li>
	 * </ol>
	 * 
	 * @param objectClass 对象类，不可为null
	 * @return 对应的属性模板，若不存在且无法生成则返回null
	 */
	@Override
	public PropertyMapping<E> getObjectTemplate(Class<?> objectClass) {
		// 第一次检查（无锁）
		PropertyMapping<E> propertyTemplate = getDelegate().get(objectClass);
		if (propertyTemplate == null && objectTemplateFactory != null
				&& objectTemplateFactory.hasObjectTemplate(objectClass)) {
			propertyTemplate = objectTemplateFactory.getObjectTemplate(objectClass);
			getDelegate().put(objectClass, propertyTemplate);
		}
		return propertyTemplate;
	}
}