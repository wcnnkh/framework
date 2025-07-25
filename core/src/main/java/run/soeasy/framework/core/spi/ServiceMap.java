package run.soeasy.framework.core.spi;

import java.util.TreeMap;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.map.MultiValueMapContainer;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 服务映射容器，基于类型映射关系管理服务实例，支持按类型层级搜索可分配的服务。
 * <p>
 * 该容器继承自{@link MultiValueMapContainer}，使用树状映射（{@link TreeMap}）存储服务，
 * 键为服务类型，值为对应类型的服务集合（{@link Services}），适用于需要按类型组织和检索服务的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射存储：使用{@link TypeComparator}对服务类型进行排序和去重</li>
 *   <li>层级搜索：通过{@link #assignableFrom}方法检索可分配给指定类型的所有服务</li>
 *   <li>动态创建：支持通过自定义函数动态创建服务集合（{@link Services}）</li>
 *   <li>线程安全：继承容器的线程安全机制，支持并发访问</li>
 * </ul>
 *
 * @param <S> 服务实例的类型
 * 
 * @author soeasy.run
 * @see MultiValueMapContainer
 * @see Services
 * @see TypeComparator
 */
public class ServiceMap<S> extends
        MultiValueMapContainer<Class<?>, S, PayloadRegistration<S>, Services<S>, TreeMap<Class<?>, Services<S>>> {
    
    /**
     * 构造函数，使用默认服务集合创建器
     * <p>
     * 默认使用{@link Services#Services()}创建服务集合实例，
     * 类型键使用{@link TypeComparator#DEFAULT}进行排序。
     */
    public ServiceMap() {
        this((key) -> new Services<>());
    }
    
    /**
     * 构造函数，使用自定义服务集合创建器
     * 
     * @param servicesCreator 服务集合创建函数，不可为null
     * @throws NullPointerException 若servicesCreator为null
     */
    public ServiceMap(@NonNull Function<? super Class<?>, ? extends Services<S>> servicesCreator) {
        super(() -> new TreeMap<>(TypeComparator.DEFAULT), servicesCreator);
    }
    
    /**
     * 搜索可分配给指定类型的服务列表
     * <p>
     * 检索所有类型与requiredType兼容的服务，包括：
     * <ol>
     *   <li>直接注册到requiredType的服务</li>
     *   <li>注册到requiredType子类型、实现类或接口的服务</li>
     * </ol>
     * 
     * @param requiredType 所需的服务类型，不可为null
     * @return 可分配的服务元素集合
     * @throws NullPointerException 若requiredType为null
     */
    public Elements<S> assignableFrom(Class<?> requiredType) {
        return readAsElements((map) -> {
            if (map == null) {
                return Elements.empty();
            }

            Services<S> services = map.get(requiredType);
            if (services != null) {
                return services;
            }

            return Elements
                    .of(() -> map.entrySet().stream().filter((e) -> ClassUtils.isAssignable(e.getKey(), requiredType))
                            .flatMap((e) -> e.getValue().stream()));
        });
    }
}