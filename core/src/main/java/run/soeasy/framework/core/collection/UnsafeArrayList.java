package run.soeasy.framework.core.collection;

import java.util.AbstractList;
import java.util.Arrays;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 不安全的ArrayList实现，提供对底层数组的直接访问能力。
 * 该类封装了一个固定大小的数组，提供List接口的访问能力，
 * 但具有以下特性：
 * <ul>
 *   <li>不支持添加/删除元素操作（继承AbstractList的默认实现）</li>
 *   <li>允许修改现有元素（通过set方法）</li>
 *   <li>toArray()方法直接返回底层数组引用，不进行复制</li>
 *   <li>提供比普通ArrayList更高的性能（避免数组复制开销）</li>
 * </ul>
 *
 * <p>使用注意事项：
 * <ul>
 *   <li>外部不应修改toArray()返回的数组，否则会影响内部状态</li>
 *   <li>线程不安全：在多线程环境下使用需要外部同步</li>
 *   <li>数组大小固定：创建后不能改变大小</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @see java.util.AbstractList
 * @see java.util.ArrayList
 */
@RequiredArgsConstructor
public class UnsafeArrayList<E> extends AbstractList<E> {
    /**
     * 底层数组，不可为null。
     * 该数组直接暴露给toArray()调用者，不进行防御性复制。
     */
    @NonNull
    private final E[] array;

    /**
     * 获取指定位置的元素。
     *
     * @param index 元素索引
     * @return 指定位置的元素
     * @throws IndexOutOfBoundsException 如果索引超出数组范围
     */
    @Override
    public E get(int index) {
        return array[index];
    }

    /**
     * 修改指定位置的元素。
     *
     * @param index   元素索引
     * @param element 新元素值
     * @return 被替换的旧元素值
     * @throws IndexOutOfBoundsException 如果索引超出数组范围
     */
    @Override
    public E set(int index, E element) {
        E old = array[index];
        array[index] = element;
        return old;
    }

    /**
     * 返回列表大小，即底层数组的长度。
     *
     * @return 列表大小
     */
    @Override
    public int size() {
        return array.length;
    }

    /**
     * 直接返回底层数组引用，不进行任何复制。
     * 警告：调用者不应修改返回的数组，否则会影响内部状态。
     *
     * @return 底层数组引用
     */
    @Override
    public E[] toArray() {
        return array;
    }

    /**
     * 将元素转换为指定类型的数组。
     * 优化处理：如果目标数组类型与内部数组类型兼容且大小足够，
     * 则直接返回内部数组，避免复制。
     *
     * @param <T> 目标数组元素类型
     * @param a   目标数组
     * @return 包含列表元素的数组
     * @throws ArrayStoreException 如果目标数组类型不兼容
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (array.length == 0) {
            if (a.length > 0) {
                a[0] = null; // 符合AbstractCollection规范
            }
            return a;
        }

        if (array.length > a.length || a.getClass().getComponentType() != array.getClass().getComponentType()) {
            // 目标数组太小或类型不兼容，创建新数组
            return (T[]) Arrays.copyOf(array, array.length, a.getClass());
        }

        // 目标数组足够大且类型兼容，复制元素
        System.arraycopy(array, 0, a, 0, array.length);
        if (a.length > array.length) {
            a[array.length] = null; // 符合AbstractCollection规范
        }
        return a;
    }

    /**
     * 重写toString方法，提供更友好的输出格式。
     * 输出格式与Arrays.toString一致。
     *
     * @return 列表的字符串表示
     */
    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}