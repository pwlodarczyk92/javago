package core.table;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import core.color.Color;
import core.table.ITable;
import core.table.Table;
import core.table.TableTest;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by maxus on 03.03.16.
 */
public class IntColTableTest extends TableTest {

	@Override
	protected ITable<Integer, Integer> createInstance(
			Collection<Integer> fields,
			Function<Integer, Collection<Integer>> adjacency,
			Color<Integer> whites,
			Color<Integer> blacks) {
		return new Table<>(fields, adjacency, whites, blacks);
	}
}
