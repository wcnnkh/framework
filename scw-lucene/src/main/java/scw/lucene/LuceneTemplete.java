package scw.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;

import scw.util.Pagination;

public interface LuceneTemplete {
	Document createDocument(Object instance);
	
	<T> T parse(Class<? extends T> type, Document document);
	
	<T> T indexWriter(IndexWriterExecutor<T> indexWriterExecutor) throws IOException;

	long createIndex(Object instance) throws IOException;

	long createIndex(Iterable<?> indexs) throws IOException;

	long deleteIndex(Query... queries) throws IOException;

	long deleteIndex(Term... terms) throws IOException;

	long updateIndex(Term term, Object index) throws IOException;

	long updateIndex(Term term, Iterable<?> indexs) throws IOException;

	<T> T indexReader(IndexReaderExecutor<T> indexReaderExecutor) throws IOException;

	<T> T indexSearcher(IndexSearchExecutor<T> indexSearchExecutor) throws IOException;

	<T> T search(Query query, int top, TopDocsMapper<T> topDocsMapper) throws IOException;

	<T> T search(Query query, int top, Sort sort, boolean doDocScores, TopFieldDocsMapper<T> topFieldDocsMapper)
			throws IOException;
	
	<T> Pagination<T> search(Query query, RowMapper<T> rowMapper, long page, int limit) throws IOException;

	<T> Pagination<T> search(Query query, Sort sort, boolean doDocScores, RowMapper<T> rowMapper, long page, int limit)
			throws IOException;
	
	<T> Pagination<T> search(Query query, Class<? extends T> resultType, long page, int limit) throws IOException;

	<T> Pagination<T> search(Query query, Sort sort, boolean doDocScores, Class<? extends T> resultType, long page,
			int limit) throws IOException;

	<T> T searchAfter(ScoreDoc after, Query query, int numHits, TopDocsMapper<T> topDocsMapper) throws IOException;

	<T> T searchAfter(ScoreDoc after, Query query, int numHits, Sort sort, boolean doDocScores,
			TopFieldDocsMapper<T> topFieldDocsMapper) throws IOException;

	<T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, RowMapper<T> rowMapper) throws IOException;

	<T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, Sort sort, boolean doDocScores,
			RowMapper<T> rowMapper) throws IOException;

	<T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, Class<? extends T> resultType)
			throws IOException;

	<T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, Sort sort, boolean doDocScores,
			Class<? extends T> resultType) throws IOException;
}