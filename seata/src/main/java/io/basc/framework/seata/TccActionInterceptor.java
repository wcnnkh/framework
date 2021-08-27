package io.basc.framework.seata;

import static io.seata.common.DefaultValues.DEFAULT_DISABLE_GLOBAL_TRANSACTION;
import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.seata.common.Constants;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import io.seata.rm.tcc.interceptor.ActionInterceptorHandler;
import io.seata.rm.tcc.remoting.RemotingDesc;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class TccActionInterceptor implements MethodInterceptor {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TccActionInterceptor.class);

	private ActionInterceptorHandler actionInterceptorHandler = new ActionInterceptorHandler();

	private volatile boolean disable = ConfigurationFactory.getInstance()
			.getBoolean(ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
					DEFAULT_DISABLE_GLOBAL_TRANSACTION);

	/**
	 * remoting bean info
	 */
	private RemotingDesc remotingDesc;

	public RemotingDesc getRemotingDesc() {
		return remotingDesc;
	}

	public void setRemotingDesc(RemotingDesc remotingDesc) {
		this.remotingDesc = remotingDesc;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if (!RootContext.inGlobalTransaction() || disable
				|| RootContext.inSagaBranch()) {
			// not in transaction
			return invoker.invoke(args);
		}
		Method method = getActionInterfaceMethod(invoker);
		TwoPhaseBusinessAction businessAction = method
				.getAnnotation(TwoPhaseBusinessAction.class);
		// try method
		if (businessAction != null) {
			// save the xid
			String xid = RootContext.getXID();
			// save the previous branchType
			BranchType previousBranchType = RootContext.getBranchType();
			// if not TCC, bind TCC branchType
			if (BranchType.TCC != previousBranchType) {
				RootContext.bindBranchType(BranchType.TCC);
			}
			try {
				// Handler the TCC Aspect
				Map<String, Object> ret = actionInterceptorHandler.proceed(
						method, args, xid, businessAction, () -> {
							return invoker.invoke(args);
						});
				// return the final result
				return ret.get(Constants.TCC_METHOD_RESULT);
			} finally {
				// if not TCC, unbind branchType
				if (BranchType.TCC != previousBranchType) {
					RootContext.unbindBranchType();
				}
			}
		}
		return invoker.invoke(args);
	}

	/**
	 * get the method from interface
	 *
	 * @param invocation
	 *            the invocation
	 * @return the action interface method
	 */
	protected Method getActionInterfaceMethod(MethodInvoker invoker) {
		Class<?> interfaceType = null;
		try {
			if (remotingDesc == null) {
				interfaceType = invoker.getDeclaringClass();
			} else {
				interfaceType = remotingDesc.getInterfaceClass();
			}
			if (interfaceType == null
					&& remotingDesc.getInterfaceClassName() != null) {
				interfaceType = Class.forName(remotingDesc
						.getInterfaceClassName(), true, Thread.currentThread()
						.getContextClassLoader());
			}
			if (interfaceType == null) {
				return invoker.getMethod();
			}
			return interfaceType.getMethod(invoker.getMethod().getName(),
					invoker.getMethod().getParameterTypes());
		} catch (NoSuchMethodException e) {
			if (interfaceType != null
					&& !invoker.getMethod().getName().equals("toString")) {
				LOGGER.warn("no such method '{}' from interface {}", invoker
						.getMethod().getName(), interfaceType.getName());
			}
			return invoker.getMethod();
		} catch (Exception e) {
			LOGGER.warn("get Method from interface failed", e);
			return invoker.getMethod();
		}
	}
}
