package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

/**
 * 可监听的注册接口
 * 继承自Listenable和Registration，支持对注册结果进行监听
 * 
 * @author shuchaowen
 * @param <T> 注册回执的类型
 */
public interface ListenableRegistration<T extends Receipt> extends Listenable<T>, Registration {

    /**
     * 可监听注册包装器接口
     * 用于包装可监听注册实例，提供代理功能
     * 
     * @param <T> 注册回执的类型
     * @param <W> 包装器自身的类型
     */
    public static interface ListenableRegistrationWrapper<T extends Receipt, W extends ListenableRegistration<T>>
            extends ListenableRegistration<T>, RegistrationWrapper<W> {

        @Override
        default Registration registerListener(Listener<T> listener) {
            return getSource().registerListener(listener);
        }

        @Override
        default ListenableRegistration<T> onComplete(Listener<? super T> listener) {
            return getSource().onComplete(listener);
        }

        @Override
        default ListenableRegistration<T> onFailure(Listener<? super T> listener) {
            return getSource().onFailure(listener);
        }

        @Override
        default ListenableRegistration<T> onSuccess(Listener<? super T> listener) {
            return getSource().onSuccess(listener);
        }
    }

    /**
     * 注册完成事件监听器
     * 
     * @param listener 完成事件监听器
     * @return 当前可监听注册实例
     */
    default ListenableRegistration<T> onComplete(Listener<? super T> listener) {
        registerListener((event) -> {
            if (event.isDone()) {
                listener.accept(event);
            }
        });
        return this;
    }

    /**
     * 注册失败事件监听器
     * 
     * @param listener 失败事件监听器
     * @return 当前可监听注册实例
     */
    default ListenableRegistration<T> onFailure(Listener<? super T> listener) {
        return onComplete((event) -> {
            if (!event.isSuccess()) {
                listener.accept(event);
            }
        });
    }

    /**
     * 注册成功事件监听器
     * 
     * @param listener 成功事件监听器
     * @return 当前可监听注册实例
     */
    default ListenableRegistration<T> onSuccess(Listener<? super T> listener) {
        return onComplete((event) -> {
            if (event.isSuccess()) {
                listener.accept(event);
            }
        });
    }
}