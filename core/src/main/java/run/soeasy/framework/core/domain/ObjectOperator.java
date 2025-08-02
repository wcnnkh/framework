package run.soeasy.framework.core.domain;

import lombok.NonNull;

/**
 * 对象操作接口，定义对象的创建、展示和克隆等操作规范。
 * 实现此接口的类可对特定类型的对象执行标准化的操作，
 * 包括对象的创建、转换为展示形式以及克隆复制等功能。
 *
 * @author soeasy.run
 * @param <T> 操作的对象类型
 */
public interface ObjectOperator<T> {
    
    /**
     * 创建一个新的对象实例。
     * 该方法通常用于初始化对象，返回的对象应处于合适的初始状态。
     *
     * @return 新创建的对象实例
     */
    T create();

    /**
     * 将源对象转换为适合对外展示的形式。
     * 该方法通常用于将内部业务对象转换为前端展示或外部接口使用的数据格式，
     * 可能涉及数据脱敏、格式转换或字段筛选等操作。
     *
     * @param source 源对象，不可为null
     * @return 转换后的展示对象
     */
    T display(@NonNull T source);

    /**
     * 克隆一个对象。
     * 该方法创建一个与源对象内容相同的新对象，通常用于对象的备份或并发处理，
     * 实现应保证克隆对象与源对象相互独立，修改不会相互影响。
     *
     * @param source 源对象，不可为null
     * @return 克隆的对象
     */
    T clone(@NonNull T source);
}