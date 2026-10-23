package run.soeasy.framework.mybatis.plugin;

import java.util.stream.IntStream;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.soeasy.framework.mybatis.entity.MybatisEntityFiller;

@RequiredArgsConstructor
@Getter
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
@Slf4j
public class FillMybatisEntityInterceptor implements Interceptor {
	@NonNull
	private final MybatisEntityFiller mybatisEntityFiller;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		int index = 0;
		mybatisEntityFiller.fillArgs((MappedStatement) args[index++],
				IntStream.range(index, args.length).mapToObj((e) -> args[e]), log);
		return invocation.proceed();
	}
}
