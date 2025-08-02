package run.soeasy.framework.core.transform.property;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.exchange.container.map.DefaultMapContainer;

/**
 * 对象模板注册表，继承自{@link DefaultMapContainer}并实现{@link ObjectTemplateFactory}，
 * 用于缓存和管理对象类与属性模板之间的映射关系，支持线程安全的模板注册和获取操作。
 * <p>
 * 该类采用读写锁机制实现并发控制，在多线程环境下保证数据一致性和高性能访问。
 * 当请求的模板不存在时，可通过配置的{@link ObjectTemplateFactory}动态生成并注册模板。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>模板缓存：基于Map结构缓存已注册的对象模板，提高重复访问效率</li>
 *   <li>线程安全：使用读写锁（{@link ReentrantReadWriteLock}）保证并发环境下的数据一致性</li>
 *   <li>懒加载：支持在首次访问时通过工厂动态生成模板并注册</li>
 * </ul>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code E}：属性类型，需实现{@link Property}接口</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>对象映射框架：缓存类与属性映射关系，避免重复反射解析</li>
 *   <li>多线程环境：在并发场景下安全地管理和获取对象模板</li>
 *   <li>动态扩展：支持运行时注册新的对象模板</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ObjectTemplateFactory
 * @see PropertyTemplate
 * @see DefaultMapContainer
 */
@Getter
@Setter
public class ObjectTemplateRegistry<E extends Property> extends DefaultMapContainer<Class<?>, PropertyTemplate<E>>
        implements ObjectTemplateFactory<E> {
    
    /** 用于动态生成对象模板的工厂，可为null */
    private volatile ObjectTemplateFactory<E> objectTemplateFactory;

    /**
     * 构造对象模板注册表
     * <p>
     * 初始化读写锁以支持并发操作。
     */
    public ObjectTemplateRegistry() {
        setReadWriteLock(new ReentrantReadWriteLock());
    }

    /**
     * 获取指定对象类的属性模板
     * <p>
     * 该方法首先检查缓存中是否存在对应的模板：
     * <ol>
     *   <li>若存在则直接返回</li>
     *   <li>若不存在且配置了工厂，则使用双重检查锁定机制创建并注册模板</li>
     * </ol>
     * 
     * @param objectClass 对象类，不可为null
     * @return 对应的属性模板，若不存在且无法生成则返回null
     */
    @Override
    public PropertyTemplate<E> getObjectTemplate(Class<?> objectClass) {
        // 第一次检查（无锁）
        PropertyTemplate<E> propertyTemplate = get(objectClass);
        if (propertyTemplate == null && objectTemplateFactory != null
                && objectTemplateFactory.hasObjectTemplate(objectClass)) {
            // 获取写锁
            Lock lock = writeLock();
            lock.lock();
            try {
                // 第二次检查（有锁）
                propertyTemplate = get(objectClass);
                if (propertyTemplate == null && objectTemplateFactory != null
                        && objectTemplateFactory.hasObjectTemplate(objectClass)) {
                    // 创建并注册模板
                    propertyTemplate = objectTemplateFactory.getObjectTemplate(objectClass);
                    put(objectClass, propertyTemplate);
                }
            } finally {
                // 释放写锁
                lock.unlock();
            }
        }
        return propertyTemplate;
    }
}