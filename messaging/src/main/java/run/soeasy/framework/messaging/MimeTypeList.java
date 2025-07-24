package run.soeasy.framework.messaging;

import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.StandardListElements;
import run.soeasy.framework.io.MimeTypeUtils;

/**
 * 媒体类型列表实现类，继承自{@link StandardListElements}以提供基于{@link MediaType}的列表操作，
 * 同时实现{@link MediaTypes}接口以支持媒体类型集合的标准行为，是存储和处理有序媒体类型列表的核心实现。
 * 
 * <p>该类封装了一个{@link List<MediaType>}作为数据源，提供以下特性：
 * - 基于元素值的无序相等性判断（忽略元素顺序，仅比较内容是否完全一致）；
 * - 重写{@link #toString()}方法，使用{@link MimeTypeUtils#toString(Collection)}生成符合HTTP规范的媒体类型字符串（如"text/plain, application/json"）；
 * - 继承{@link StandardListElements}的所有列表操作能力，同时遵循{@link MediaTypes}接口定义的媒体类型集合行为。
 * 
 * @author soeasy.run
 * @see MediaTypes
 * @see StandardListElements
 * @see MediaType
 */
public class MimeTypeList extends StandardListElements<MediaType, List<MediaType>>
        implements MediaTypes {
    private static final long serialVersionUID = 1L;

    /**
     * 基于指定的媒体类型列表创建{@code MimeTypeList}实例
     * 
     * <p>注意：构造时不会自动排序，需确保传入的列表已按预期顺序（通常是按{@link MediaType#SPECIFICITY_COMPARATOR}排序）排列。
     * 
     * @param source 媒体类型列表（非空，列表内容可修改，外部修改会影响当前实例）
     */
    public MimeTypeList(@NonNull List<MediaType> source) {
        super(source);
    }

    /**
     * 判断当前媒体类型列表与另一个对象是否相等
     * 
     * <p>相等性判断为无序比较：两个对象若都是{@link MediaTypes}实例，且包含的媒体类型元素完全相同（忽略顺序），则视为相等。
     * 
     * @param obj 待比较的对象（可为null）
     * @return 相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof MediaTypes) {
            return CollectionUtils.unorderedEquals(toList(), ((MediaTypes) obj).toList());
        }
        return false;
    }

    /**
     * 将当前媒体类型列表转换为符合HTTP规范的字符串表示（逗号分隔的媒体类型）
     * 
     * <p>例如：包含{@code text/plain}和{@code application/json}的列表会转换为"text/plain, application/json"。
     * 
     * @return 逗号分隔的媒体类型字符串（非空，空列表返回空字符串）
     */
    @Override
    public String toString() {
        return MimeTypeUtils.toString(this);
    }

}