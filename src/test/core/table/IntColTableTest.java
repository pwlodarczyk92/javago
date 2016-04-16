package core.table;

import core.color.Color;

import java.util.Collection;
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
