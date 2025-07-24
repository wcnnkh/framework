package run.soeasy.framework.core.exchange;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;

/**
 * 表示已注册状态的不可变注册操作实现，实现了{@link Registration}接口和{@link Serializable}接口。
 * 该类用于表示一个已完成的注册操作，其取消状态在创建时确定，之后不可更改。
 *
 * <p>设计特点：
 * <ul>
 *   <li>不可变对象：创建后状态不可修改</li>
 *   <li>确定性取消状态：通过构造参数指定初始取消状态</li>
 *   <li>不可取消：一旦创建，无法通过cancel()方法更改状态</li>
 *   <li>序列化支持：实现Serializable接口，支持跨进程传输</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>作为注册操作的静态表示</li>
 *   <li>在序列化场景中传递注册状态</li>
 *   <li>用于测试环境中的模拟对象</li>
 *   <li>表示不可取消的系统级注册</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Registration
 * @see Serializable
 */
@RequiredArgsConstructor
public class Registed implements Registration, Serializable {

    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 注册操作的取消状态
     */
    private final boolean cancelled;

    /**
     * 尝试取消注册操作
     * 由于该实现不可取消，此方法始终返回false且不会更改内部状态
     * 
     * @return false，表示取消操作失败
     */
    @Override
    public boolean cancel() {
        return false;
    }

    /**
     * 判断注册操作是否可取消
     * 由于该实现不可取消，此方法始终返回false
     * 
     * @return false，表示不可取消
     */
    @Override
    public boolean isCancellable() {
        return false;
    }

    /**
     * 判断注册操作是否已取消
     * 返回创建时通过构造参数指定的取消状态，之后不会改变
     * 
     * @return 注册操作的取消状态
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}