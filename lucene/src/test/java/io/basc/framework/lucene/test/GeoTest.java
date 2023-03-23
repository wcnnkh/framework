package io.basc.framework.lucene.test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.junit.Test;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;

import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.lucene.DefaultLuceneTemplate;
import io.basc.framework.lucene.LuceneTemplate;
import io.basc.framework.lucene.LuceneWriteException;
import io.basc.framework.lucene.SearchParameters;
import io.basc.framework.lucene.SearchResults;

public class GeoTest {
	private final LuceneTemplate luceneTemplate = new DefaultLuceneTemplate("geo");
	private SpatialContext spatialContext = SpatialContext.GEO;
	private SpatialStrategy strategy;

	@Test
	public void test() throws LuceneWriteException, InterruptedException, ExecutionException {
		LoggerFactory.getSource().getLevelManager().getSourceMap().put("io.basc.framework.util.concurrent.TaskQueue",
				Levels.DEBUG.getValue());

		// SpatialPrefixTree也可以通过SpatialPrefixTreeFactory工厂类构建
		SpatialPrefixTree grid = new GeohashPrefixTree(spatialContext, GeohashPrefixTree.getMaxLevelsPossible());
		this.strategy = new RecursivePrefixTreeStrategy(grid, "geoField");

		Document document = new Document();
		Point point = spatialContext.getShapeFactory().pointXY(116.409788d, 39.878675d);
		Field[] fields = strategy.createIndexableFields(point);
		for (Field field : fields) {
			document.add(field);
		}
		String id = "test";
		document.add(new StringField("id", id, Store.YES));
		Long count = luceneTemplate.saveOrUpdate(new Term("id", id), document).get();
		System.out.println("保存成功：" + count);
		Shape shape = spatialContext.getShapeFactory().circle(116.409788d, 39.878674d,
				DistanceUtils.dist2Degrees(1000000, DistanceUtils.EARTH_MEAN_RADIUS_KM));
		SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, shape);
		Query luceneQuery = strategy.makeQuery(args);
		SearchParameters parameters = new SearchParameters(luceneQuery, 10);
		SearchResults<Document> results = luceneTemplate.search(parameters, Document.class);
		List<Document> list = results.all().getList();
		for (Document d : list) {
			System.out.println(d.getFields());
		}
	}
}
