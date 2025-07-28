package run.soeasy.framework.messaging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.io.MimeType;

/**
 * 媒体类型集合接口，继承{@link Elements}以提供对{@link MediaType}元素的集合操作，
 * 同时实现{@link Comparable}支持按特异性排序后的集合比较，是处理多个媒体类型（如HTTP请求头中的Accept列表）的核心接口。
 * 
 * <p>核心功能：
 * - 提供多种静态工厂方法（如{@link #forString(String)}、{@link #forArray(String...)}），
 *   支持从字符串、列表、数组等解析并创建{@code MediaTypes}实例，默认按媒体类型特异性排序；
 * - 支持集合级别的兼容性检查（{@link #isCompatibleWith(MimeType)}），判断集合中是否存在与指定媒体类型兼容的元素；
 * - 实现比较逻辑，基于排序后的媒体类型列表进行集合间的比较。
 * 
 * @author soeasy.run
 * @see MediaType
 * @see Elements
 */
public interface MediaTypes extends Elements<MediaType>, Comparable<MediaTypes> {

    /**
     * 空媒体类型集合常量（不可变）
     */
    public static final MediaTypes EMPTY = new EmptyMimeTypes();

    /**
     * 基于已有元素集合创建{@code MediaTypes}实例，先按媒体类型特异性排序再构造
     * 
     * @param elements 包含{@link MediaType}元素的集合（非空）
     * @return 排序后的{@code MediaTypes}实例（非空）
     */
    public static MediaTypes forElements(@NonNull Elements<? extends MediaType> elements) {
        List<MediaType> list = elements.sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
        return forSortedList(list);
    }

    /**
     * 基于已排序的列表创建{@code MediaTypes}实例，保持列表的原始顺序（需确保已按特异性排序）
     * 
     * @param list 已排序的{@link MediaType}列表（非空）
     * @return 基于该列表的{@code MediaTypes}实例（非空）
     */
    public static MediaTypes forSortedList(@NonNull List<MediaType> list) {
        if (list.isEmpty()) {
            return EMPTY;
        }
        return new MimeTypeList(list);
    }

    /**
     * 解析逗号分隔的媒体类型字符串创建{@code MediaTypes}实例（如解析"text/html,application/json"）
     * 
     * @param mediaType 逗号分隔的媒体类型字符串（可为空，空字符串返回{@link #EMPTY}）
     * @return 解析并排序后的{@code MediaTypes}实例
     * @throws InvalidMediaTypeException 若字符串解析失败（如包含非法格式的媒体类型）
     */
    public static MediaTypes forString(String mediaType) {
        List<MediaType> list = MediaType.parseMediaTypes(mediaType);
        list.sort(MediaType.SPECIFICITY_COMPARATOR);
        return forSortedList(list);
    }

    /**
     * 解析字符串列表创建{@code MediaTypes}实例（每个字符串可能是逗号分隔的多类型）
     * 
     * @param mediaTypeList 媒体类型字符串列表（可为空，空列表返回{@link #EMPTY}）
     * @return 解析并排序后的{@code MediaTypes}实例
     * @throws InvalidMediaTypeException 若任一字符串解析失败
     */
    public static MediaTypes forList(List<? extends String> mediaTypeList) {
        List<MediaType> list = MediaType.parseMediaTypes(mediaTypeList);
        list.sort(MediaType.SPECIFICITY_COMPARATOR);
        return forSortedList(list);
    }

    /**
     * 解析字符串数组创建{@code MediaTypes}实例
     * 
     * @param mediaType 媒体类型字符串数组（可为空，空数组返回{@link #EMPTY}）
     * @return 解析并排序后的{@code MediaTypes}实例
     * @throws InvalidMediaTypeException 若任一字符串解析失败
     */
    public static MediaTypes forArray(String... mediaType) {
        return forList(Arrays.asList(mediaType));
    }

    /**
     * 比较两个{@code MediaTypes}实例，基于排序后的媒体类型列表按顺序比较元素
     * 
     * <p>比较规则：遍历两个集合中排序后的元素，首个不相等的元素的比较结果即为集合的比较结果；
     * 若一个集合是另一个的前缀且所有元素相等，则元素少的集合视为较小。
     * 
     * @param o 另一个{@code MediaTypes}实例
     * @return 负整数、零、正整数分别表示当前实例小于、等于、大于另一个实例
     */
    @Override
    default int compareTo(MediaTypes o) {
        if (this.equals(o)) {
            return 0;
        }

        // 遍历两个集合的元素（已按特异性排序），比较首个不相等的元素
        List<MediaType> thisList = this.collect(Collectors.toList());
        List<MediaType> otherList = o.collect(Collectors.toList());
        int minSize = Math.min(thisList.size(), otherList.size());
        for (int i = 0; i < minSize; i++) {
            MediaType thisMediaType = thisList.get(i);
            MediaType otherMediaType = otherList.get(i);
            int compare = thisMediaType.compareTo(otherMediaType);
            if (compare != 0) {
                return compare;
            }
        }
        // 若前缀元素均相等，元素少的集合视为较小
        return Integer.compare(thisList.size(), otherList.size());
    }

    @Override
    boolean equals(Object obj);

    /**
     * 获取当前媒体类型集合中所有元素的原始字符串表示（按排序后的顺序）
     * 
     * @return 包含每个{@link MediaType#toString()}结果的{@link Elements}（非空）
     */
    default Elements<String> getRawElements() {
        if (isEmpty()) {
            return Elements.empty();
        }
        return map(MediaType::toString);
    }

    @Override
    int hashCode();

    /**
     * 判断当前媒体类型集合中是否存在与指定{@link MimeType}兼容的元素
     * 
     * <p>兼容性基于{@link MediaType#isCompatibleWith(MimeType)}判断，即集合中任一元素与指定类型兼容则返回true。
     * 
     * @param mimeType 待检查的{@link MimeType}（非空）
     * @return 存在兼容元素返回true，否则返回false
     */
    default boolean isCompatibleWith(MimeType mimeType) {
        for (MediaType mime : this) {
            if (mime.isCompatibleWith(mimeType)) {
                return true;
            }
        }
        return false;
    }
}