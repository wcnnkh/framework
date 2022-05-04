package io.basc.framework.data.repository;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.MethodParameter;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CharSequenceSplitSegment;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.stream.StreamProcessorSupport;
import sun.swing.SwingUtilities2.Section;

public class RepositoryMethodInterceptor implements MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(RepositoryMethodInterceptor.class);

	private static final Set<String> QUERY_KEYS = new HashSet<String>(Arrays.asList("select", "query", "get"));
	private static final Set<String> UPDATE_KEYS = new HashSet<String>(Arrays.asList("update"));
	private static final Set<String> DELETE_KEYS = new HashSet<String>(Arrays.asList("delete"));
	private static final Set<String> SAVE_KEYS = new HashSet<String>(Arrays.asList("save"));
	private static final Set<String> ORDER_BY_KEYS = new HashSet<String>(Arrays.asList("orderBy"));
	private static final Set<String> CONDITION_KEYS = new HashSet<String>(Arrays.asList("and", "or"));

	private static final Set<String> EQU_KEYS = new HashSet<String>(Arrays.asList("Equ"));
	private static final Set<String> NEQ_KEYS = new HashSet<String>(Arrays.asList("Neq"));
	private static final Set<String> LSS_KEYS = new HashSet<String>(Arrays.asList("Lss"));
	private static final Set<String> LEQ_KEYS = new HashSet<String>(Arrays.asList("Leq"));
	private static final Set<String> GTR_KEYS = new HashSet<String>(Arrays.asList("Gtr"));
	private static final Set<String> GEQ_KEYS = new HashSet<String>(Arrays.asList("Geq"));

	private final Repository repository;
	private final Set<String> queryKeys = new HashSet<String>(4);
	private final Set<String> updateKeys = new HashSet<String>(4);
	private final Set<String> deleteKeys = new HashSet<String>(4);
	private final Set<String> saveKeys = new HashSet<String>(4);
	private final Set<String> orderByKeys = new HashSet<String>(4);
	private final Set<String> equKeys = new HashSet<String>();
	private final Set<String> neqKeys = new HashSet<String>();
	private final Set<String> lssKeys = new HashSet<String>();
	private final Set<String> leqKeys = new HashSet<String>();
	private final Set<String> gtrKeys = new HashSet<String>();
	private final Set<String> geqKeys = new HashSet<String>();

	public RepositoryMethodInterceptor(Repository repository) {
		this.repository = repository;
	}

	public Set<String> getQueryKeys() {
		return queryKeys;
	}

	public Set<String> getUpdateKeys() {
		return updateKeys;
	}

	public Set<String> getDeleteKeys() {
		return deleteKeys;
	}

	public Set<String> getSaveKeys() {
		return saveKeys;
	}

	public Repository getRepository() {
		return repository;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		if (!(Modifier.isAbstract(invoker.getMethod().getModifiers())
				|| Modifier.isInterface(invoker.getMethod().getModifiers()))) {
			return invoker.invoke(args);
		}

		for (String key : queryKeys) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(1, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}

		for (String key : QUERY_KEYS) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(1, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}

		for (String key : updateKeys) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(2, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}

		for (String key : UPDATE_KEYS) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(2, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}
		for (String key : deleteKeys) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(3, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}

		for (String key : DELETE_KEYS) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(3, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}
		for (String key : saveKeys) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(4, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}

		for (String key : SAVE_KEYS) {
			if (invoker.getMethod().getName().startsWith(key)) {
				return process(4, invoker.getMethod().getName().substring(key.length()), invoker, args);
			}
		}
		throw new NotSupportedException(invoker.toString());
	}

	protected void appendColumns(List<RepositoryColumn> columns, ParameterDescriptor descriptor, Object value) {
		columns.add(new RepositoryColumn(descriptor.getName(), value, new TypeDescriptor(descriptor)));
	}

	private List<RepositoryColumn> parseColumns(MethodInvoker invoker, Object[] args) {
		List<RepositoryColumn> columns = new ArrayList<RepositoryColumn>();
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameters(invoker.getMethod());
		for (int i = 0; i < parameterDescriptors.length; i++) {
			appendColumns(columns, parameterDescriptors[i], args[i]);
		}
		return columns;
	}

	private Object process(int opType, String express, MethodInvoker invoker, Object[] args) throws Throwable {
		Pair<String, Integer> orderBy = indexOf(express, Arrays.asList(orderByKeys, ORDER_BY_KEYS));
		List<OrderColumn> columns = new ArrayList<OrderColumn>();
		if (orderBy != null) {
			List<CharSequenceSplitSegment> list = StringUtils
					.split(express.subSequence(orderBy.getValue() + orderBy.getKey().length(), express.length()),
							"Desc", "Aes")
					.collect(Collectors.toList());
			if (list.size() == 1) {
				CharSequenceSplitSegment segment = list.get(0);
				columns.add(new OrderColumn(segment.toString(), segment.getSeparator() == null ? Sort.DESC
						: Sort.valueOf(segment.getSeparator().toString().toUpperCase()), null));
			} else {
				for (CharSequenceSplitSegment segment : list) {
					columns.add(new OrderColumn(segment.toString(), segment.getSeparator() == null ? Sort.DESC
							: Sort.valueOf(segment.getSeparator().toString().toUpperCase()), null));
				}
			}
			express = express.substring(0, orderBy.getValue());
		}

		
		Conditions conditions = null;
		while (express != null && express.length() != 0) {
			Pair<String, Integer> equ = indexOf(express, Arrays.asList(equKeys, EQU_KEYS));
			if (equ != null) {
				String name = express.substring(0, equ.getValue());
				
			}
		}
	}

	private Pair<String, Integer> indexOf(String express, Collection<? extends Collection<String>> keys) {
		for (Collection<String> key : keys) {
			Pair<String, Integer> pair = StreamProcessorSupport.process(key, (e) -> express.indexOf(e),
					(e) -> e.getValue() != -1);
			if (pair != null && pair.getValue() != -1) {
				return pair;
			}
		}
		return null;
	}
}
