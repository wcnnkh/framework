package run.soeasy.framework.core.exchange;

import lombok.NonNull;

/**
 * 操作回执接口
 * 表示异步操作的结果状态，支持操作取消和状态查询
 * 
 * @author shuchaowen
 */
public interface Receipt extends Registration {

    /** 表示操作失败的常量回执 */
    public static final Receipt FAILURE = new Receipted(false, null);
    /** 表示操作成功的常量回执 */
    public static final Receipt SUCCESS = new Receipted(true, null);

    /**
     * 创建失败状态的回执
     * 
     * @param cause 失败原因，可为null
     * @return 失败状态的回执实例
     */
    public static Receipt failure(Throwable cause) {
        return new Receipted(false, cause);
    }

    /**
     * 创建成功状态的回执（带可选的异常信息）
     * 
     * @param cause 可能的异常信息，可为null
     * @return 成功状态的回执实例
     */
    public static Receipt success(Throwable cause) {
        return new Receipted(true, cause);
    }

    /**
     * 创建关联注册操作的成功回执
     * 
     * @param registration 注册操作句柄，不可为null
     * @return 关联注册操作的成功回执
     */
    public static Receipt success(@NonNull Registration registration) {
        return new SuccessfullyRegistered(registration);
    }

    /**
     * 获取操作异常信息
     * 
     * @return 异常原因，操作成功或无异常时返回null
     */
    Throwable cause();

    /**
     * 判断操作是否已完成
     * 
     * @return 操作完成返回true，否则返回false
     */
    boolean isDone();

    /**
     * 判断操作是否成功
     * 
     * @return 操作成功返回true，否则返回false
     */
    boolean isSuccess();

    /**
     * 同步操作结果
     * 确保所有异步处理完成后返回最终状态
     * 
     * @return 同步后的回执实例
     */
    default Receipt sync() {
        return this;
    }
}