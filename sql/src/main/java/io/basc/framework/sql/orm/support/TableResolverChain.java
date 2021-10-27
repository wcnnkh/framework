package io.basc.framework.sql.orm.support;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableExtend;
import io.basc.framework.sql.orm.TableResolver;

import java.util.Iterator;

public class TableResolverChain implements TableResolver{
	private final Iterator<TableExtend> iterator;
	private final TableResolver nextChain;
	
	public TableResolverChain(Iterator<TableExtend> iterator, TableResolver nextChain){
		this.iterator = iterator;
		this.nextChain = nextChain;
	}
	
	@Override
	public Range<Double> getRange(Class<?> entityClass,
			FieldDescriptor descriptor) {
		if(iterator.hasNext()){
			return iterator.next().getRange(entityClass, descriptor, this);
		}
		
		return nextChain == null? null:nextChain.getRange(entityClass, descriptor);
	}
	
	@Override
	public IndexInfo getIndex(Class<?> entityClass, FieldDescriptor descriptor) {
		if(iterator.hasNext()){
			return iterator.next().getIndex(entityClass, descriptor, this);
		}
		return nextChain == null? null:nextChain.getIndex(entityClass, descriptor);
	}

}
