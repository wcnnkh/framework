package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.value.TypedData;

/**
 * 带实体内容的消息接口，继承自{@link Message}，表示包含具体数据体的消息，
 * 适用于需要携带业务数据的场景（如HTTP请求体、响应体，或消息队列中的带数据消息）。
 * 
 * <p>该接口通过{@link #getBody()}方法暴露实体数据，数据类型由泛型参数{@code T}指定，
 * 并通过{@link TypedData}封装，支持类型安全的数据访问和转换。
 * 
 * @param <T> 实体数据的类型
 * @author soeasy.run
 * @see Message
 * @see TypedData
 */
public interface Entity<T> extends Message {

    /**
     * 获取消息的实体数据，封装为{@link TypedData}以支持类型化访问
     * 
     * <p>返回的{@code TypedData<T>}包含实体的原始数据及类型信息，可用于安全地获取特定类型的数据，
     * 例如从HTTP请求体中解析JSON为Java对象。
     * 
     * @return 实体数据的类型化封装（非空，可能包含空数据）
     */
    TypedData<T> getBody();
}