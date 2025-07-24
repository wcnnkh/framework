package run.soeasy.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.LRULinkedHashMap;
import run.soeasy.framework.core.execute.reflect.ReflectionMethod;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 合并注解实现类，继承自{@link AbstractAnnotationPropertyMapping}并实现{@link Serializable}接口，
 * 用于将多个同类型注解合并为一个逻辑注解，提供统一的属性访问接口，是框架中处理复合注解的核心组件。
 * <p>
 * 该类通过反射缓存和流式处理优化注解合并性能，支持将多个注解的属性合并为一个逻辑注解，
 * 适用于需要处理重复注解或复合注解的场景，如Spring风格的组合注解、测试框架的多层注解合并等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>注解合并：将多个同类型注解合并为一个逻辑注解，属性值按顺序合并</li>
 *   <li>缓存优化：使用LRU缓存注解方法，避免重复反射调用</li>
 *   <li>流式处理：通过Stream API高效处理多个注解的属性合并</li>
 *   <li>序列化支持：实现Serializable接口，支持跨进程的注解合并场景</li>
 *   <li>线程安全：使用双重检查锁确保缓存操作的线程安全性</li>
 * </ul>
 * </p>
 *
 * <p><b>泛型说明：</b>
 * <ul>
 *   <li>{@code A}：合并的注解类型，必须是{@link Annotation}的子类</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>组合注解：将多个基础注解合并为一个复合注解</li>
 *   <li>重复注解：处理JDK 8+的重复注解场景</li>
 *   <li>AOP切面：合并类和方法上的同类型注解</li>
 *   <li>测试框架：动态合并测试相关的多个注解</li>
 *   <li>元编程：基于多个注解生成统一的元数据描述</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see AbstractAnnotationPropertyMapping
 * @see Annotation
 */
@RequiredArgsConstructor
@Getter
public class MergedAnnotation<A extends Annotation> extends AbstractAnnotationPropertyMapping<A>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 注解方法缓存，使用LRU算法淘汰最少使用的缓存项 */
    private static Map<Class<?>, Method[]> methodsMap = new LRULinkedHashMap<>(256);

    /**
     * 获取注解类型的方法数组（带缓存优化）
     * <p>
     * 该方法使用双重检查锁机制缓存注解方法，避免重复反射调用：
     * <ol>
     *   <li>首次访问时通过反射获取方法数组</li>
     *   <li>使用synchronized块确保多线程环境下的安全初始化</li>
     *   <li>过滤掉Object类的方法（如equals/hashCode）</li>
     * </ol>
     * </p>
     * 
     * @param annotationType 注解类型
     * @return 过滤后的方法数组
     */
    private static Method[] getMethods(Class<? extends Annotation> annotationType) {
        Method[] methods = methodsMap.get(annotationType);
        if (methods == null) {
            synchronized (methodsMap) {
                methods = methodsMap.get(annotationType);
                if (methods == null) {
                    methods = annotationType.getMethods();
                    if (methods != null && methods.length > 0) {
                        List<Method> list = new ArrayList<>(methods.length);
                        for (Method method : methods) {
                            if (ReflectionUtils.isObjectMethod(method)) {
                                continue;
                            }
                            list.add(method);
                        }
                        methods = list.toArray(new Method[0]);
                    }
                    methodsMap.put(annotationType, methods);
                }
            }
        }
        return methods;
    }

    /** 合并的注解类型 */
    @NonNull
    private final Class<A> type;
    
    /** 要合并的注解列表 */
    @NonNull
    private final Iterable<? extends A> annotations;

    /**
     * 获取属性访问器迭代器（合并所有注解的属性）
     * <p>
     * 该方法通过流式处理合并多个注解的属性访问器：
     * <ol>
     *   <li>遍历所有注解实例</li>
     *   <li>获取每个注解的方法并转换为PropertyAccessor</li>
     *   <li>使用Stream.flatMap合并所有访问器</li>
     * </ol>
     * </p>
     * 
     * @return 属性访问器迭代器
     */
    @Override
    public Iterator<PropertyAccessor> iterator() {
        return CollectionUtils.unknownSizeStream(annotations.iterator()).flatMap((annotation) -> {
            Method[] methods = getMethods(type);
            return Arrays.asList(methods).stream()
                    .map((method) -> new ReflectionMethod(method).accessor(annotation));
        }).iterator();
    }

    /**
     * 返回合并注解的字符串表示
     * 
     * @return 注解列表的字符串表示
     */
    @Override
    public String toString() {
        return annotations.toString();
    }

    /**
     * 计算合并注解的哈希值
     * <p>
     * 哈希值计算基于：
     * <ol>
     *   <li>注解类型的哈希值</li>
     *   <li>所有注解实例的哈希值（通过ArrayUtils.hashCode计算）</li>
     * </ol>
     * </p>
     * 
     * @return 合并注解的哈希值
     */
    @Override
    public int hashCode() {
        return type.hashCode() + ArrayUtils.hashCode(annotations);
    }

    /**
     * 判断与其他对象的相等性
     * <p>
     * 相等性判断逻辑：
     * <ol>
     *   <li>若对象为单个Annotation实例，先转换为MergedAnnotation再比较</li>
     *   <li>否则调用父类的相等性判断（基于类型和属性值）</li>
     * </ol>
     * </p>
     * 
     * @param obj 待比较的对象
     * @return 相等返回true，否则返回false
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Annotation) {
            Annotation annotation = (Annotation) obj;
            MergedAnnotation mergedAnnotation = new MergedAnnotation(annotation.getClass(), Arrays.asList(annotation));
            return equals(mergedAnnotation);
        }
        return super.equals(obj);
    }
}