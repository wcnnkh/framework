package run.soeasy.framework.core.exchange;

/**
 * 最终状态的回执实现，用于表示操作的最终结果状态。
 * 该类继承自{@link Registed}并实现{@link Receipt}接口，
 * 提供操作完成状态、成功状态以及可能的异常原因。
 *
 * <p>设计特点：
 * <ul>
 *   <li>不可变对象：创建后状态不可修改</li>
 *   <li>多状态组合：支持未完成、已完成成功、已完成失败等多种状态</li>
 *   <li>确定性取消状态：始终返回false，表示不支持取消操作</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>异步操作的结果反馈</li>
 *   <li>批量操作的汇总状态</li>
 *   <li>服务调用的返回结果</li>
 *   <li>系统事件的处理回执</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Receipt
 * @see Registed
 */
public class Receipted extends Registed implements Receipt {
    private static final long serialVersionUID = 1L;
    
    /**
     * 操作完成状态
     */
    private final boolean done;
    
    /**
     * 操作成功状态
     */
    private final boolean success;
    
    /**
     * 操作失败的原因
     */
    private final Throwable cause;

    /**
     * 创建一个未完成的回执
     * 初始状态：未完成、未取消、无异常
     */
    public Receipted() {
        this(false, false, null);
    }

    /**
     * 创建一个已完成的回执
     * 
     * @param success 操作是否成功
     * @param cause 操作失败的原因，如果成功则为null
     */
    public Receipted(boolean success, Throwable cause) {
        this(true, success, cause);
    }

    /**
     * 受保护的构造方法，用于创建自定义状态的回执
     * 
     * @param done 操作是否完成
     * @param success 操作是否成功
     * @param cause 操作失败的原因，如果成功则为null
     */
    protected Receipted(boolean done, boolean success, Throwable cause) {
        super(false);
        this.done = done;
        this.success = success;
        this.cause = cause;
    }

    /**
     * 判断操作是否已完成
     * 
     * @return 如果操作已完成返回true，否则返回false
     */
    @Override
    public boolean isDone() {
        return done;
    }

    /**
     * 判断操作是否成功
     * 仅当操作已完成且成功状态为true时返回true
     * 
     * @return 如果操作成功返回true，否则返回false
     */
    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取操作失败的原因
     * 
     * @return 操作失败的异常原因，如果操作成功或未完成则返回null
     */
    @Override
    public Throwable cause() {
        return cause;
    }

    /**
     * 判断操作是否已取消
     * 该实现始终返回false，表示不支持取消操作
     * 
     * @return false
     */
    @Override
    public boolean isCancelled() {
        return false;
    }
}