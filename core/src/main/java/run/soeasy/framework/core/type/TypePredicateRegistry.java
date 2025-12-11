package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

import run.soeasy.framework.core.exchange.CollectionContainer;
import run.soeasy.framework.core.exchange.Operation;

/**
 * Type类型谓词注册表，管理{@link Predicate<Type>}类型的谓词集合，
 * 实现{@link Predicate<Type>}接口提供Type类型匹配能力，底层默认使用
 * {@link CopyOnWriteArraySet}保证并发安全。
 *
 * @author soeasy.run
 */
public class TypePredicateRegistry
        extends CollectionContainer<Predicate<? super Type>, CopyOnWriteArraySet<Predicate<? super Type>>>
        implements Predicate<Type> {

    /**
     * 构造TypePredicateRegistry实例，默认创建{@link CopyOnWriteArraySet}作为底层存储容器。
     */
    public TypePredicateRegistry() {
        super(new CopyOnWriteArraySet<>());
    }

    /**
     * 判断指定Type对象是否匹配注册表中任意一个谓词。
     *
     * @param t 待匹配的Type对象
     * @return 存在匹配的谓词返回true，否则返回false
     */
    @Override
    public boolean test(Type t) {
        return anyMatch((e) -> e.test(t));
    }

    /**
     * 注册指定Type的等值匹配谓词。
     *
     * @param type 待注册等值匹配规则的Type对象
     * @return 用于操作已注册谓词的{@link Operation}实例
     */
    public Operation registerType(Type type) {
        return register(Predicate.isEqual(type));
    }
}