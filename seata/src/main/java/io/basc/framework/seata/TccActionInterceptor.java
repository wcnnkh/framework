package io.basc.framework.seata;

import static io.seata.common.DefaultValues.DEFAULT_DISABLE_GLOBAL_TRANSACTION;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.execution.test.Executor;
import io.basc.framework.util.element.Elements;
import io.seata.common.Constants;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import io.seata.rm.tcc.interceptor.ActionInterceptorHandler;
import io.seata.rm.tcc.remoting.RemotingDesc;

public class TccActionInterceptor implements ExecutionInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TccActionInterceptor.class);

	private ActionInterceptorHandler actionInterceptorHandler = new ActionInterceptorHandler();

	private volatile boolean disable = ConfigurationFactory.getInstance()
			.getBoolean(ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION, DEFAULT_DISABLE_GLOBAL_TRANSACTION);

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

	/**
	 * get the method from interface
	 *
	 * @param invoker the invocation
	 * @return the action interface method
	 */
	protected Method getActionInterfaceMethod(ReflectionMethod invoker) {
		Class<?> interfaceType = null;
		try {
			if (remotingDesc == null) {
				interfaceType = invoker.getSource().getType();
			} else {
				interfaceType = remotingDesc.getInterfaceClass();
			}
			if (interfaceType == null && remotingDesc.getInterfaceClassName() != null) {
				interfaceType = Class.forName(remotingDesc.getInterfaceClassName(), true,
						Thread.currentThread().getContextClassLoader());
			}
			if (interfaceType == null) {
				return invoker.getExecutable();
			}
			return interfaceType.getMethod(invoker.getExecutable().getName(),
					invoker.getExecutable().getParameterTypes());
		} catch (NoSuchMethodException e) {
			if (interfaceType != null && !invoker.getExecutable().getName().equals("toString")) {
				LOGGER.warn("no such method '{}' from interface {}", invoker.getExecutable().getName(),
						interfaceType.getName());
			}
			return invoker.getExecutable();
		} catch (Exception e) {
			LOGGER.warn("get Method from interface failed", e);
			return invoker.getExecutable();
		}
	}

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (!(executor instanceof ReflectionMethod)) {
			return executor.execute(args);
		}

		if (!RootContext.inGlobalTransaction() || disable || RootContext.inSagaBranch()) {
			// not in transaction
			return executor.execute(args);
		}

		ReflectionMethod methodExecutor = (ReflectionMethod) executor;
		Method method = getActionInterfaceMethod(methodExecutor);
		TwoPhaseBusinessAction businessAction = method.getAnnotation(TwoPhaseBusinessAction.class);
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
				Map<String, Object> ret = actionInterceptorHandler.proceed(method, args.toArray(), xid, businessAction,
						() -> {
							return methodExecutor.execute(args);
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
		return methodExecutor.execute(args);
	}
}
