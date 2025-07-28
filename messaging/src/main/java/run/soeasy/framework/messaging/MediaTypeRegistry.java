package run.soeasy.framework.messaging;

import run.soeasy.framework.core.exchange.container.map.TreeSetContainer;

/**
 * 媒体类型注册表，继承自{@link TreeSetContainer}以提供基于{@link MediaType}的有序集合管理，
 * 同时实现{@link MediaTypes}接口以支持媒体类型集合的标准操作，是管理系统支持的媒体类型的核心组件。
 * 
 * <p>该类默认使用{@link MediaType#SPECIFICITY_COMPARATOR}作为排序器，确保集合中的媒体类型
 * 始终按特异性排序（具体类型优先于通配符类型，如{@code text/plain} &gt; {@code text/*} &gt; {@code *&#47;*}）。
 * 
 * <p>主要用途：
 * - 集中管理系统支持的媒体类型（如注册、查询、移除）；
 * - 提供与{@link MediaTypes}接口兼容的操作，便于与其他媒体类型集合组件交互；
 * - 通过{@link #toList()}方法转换为不可变的{@link MimeTypeList}实例，用于安全传输或展示。
 * 
 * @author soeasy.run
 * @see MediaType
 * @see MediaTypes
 * @see TreeSetContainer
 */
public class MediaTypeRegistry extends TreeSetContainer<MediaType> implements MediaTypes {

    /**
     * 构造一个空的媒体类型注册表，默认使用{@link MediaType#SPECIFICITY_COMPARATOR}排序
     */
    public MediaTypeRegistry() {
        setComparator(MediaType.SPECIFICITY_COMPARATOR);
    }

    /**
     * 将当前注册表中的媒体类型转换为不可变的{@link MimeTypeList}列表
     * 
     * <p>返回的列表保持与注册表相同的排序（按媒体类型特异性），且不可修改，适合用于需要固定集合视图的场景。
     * 
     * @return 包含当前所有媒体类型的{@link MimeTypeList}实例（非空，可能为空列表）
     */
    @Override
    public MimeTypeList toList() {
        return new MimeTypeList(super.toList());
    }
}