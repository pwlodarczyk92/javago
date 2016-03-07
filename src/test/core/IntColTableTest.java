package core;

import core.table.Table;
import core.color.IntColor;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 03.03.16.
 */
public class IntColTableTest extends TableTest<Integer, IntColor, Table<Integer, Integer, IntColor>> {
	@Override
	protected Table<Integer, Integer, IntColor> createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		return new Table<>(adjacency, whites, blacks);
	}
}
