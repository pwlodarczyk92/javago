package core.table;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import core.Stone;
import core.color.Color;
import core.color.ColorView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Created by maxus on 03.03.16.
 */
public abstract class TableTest {

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adjacency = new LinAdj();

	protected abstract ITable<Integer, ?> createInstance(Collection<Integer> fields, Function<Integer, Collection<Integer>> adjacency, Color<Integer> whites, Color<Integer> blacks);

	@org.junit.Test
	public final void simpleTest() {
		List<Integer> range = ContiguousSet.create(Range.closed(-20, 20), DiscreteDomain.integers()).asList();
		ITable<Integer, ?> table = createInstance(range, adjacency, new Color<>(adjacency), new Color<>(adjacency));
		ColorView<Integer, ?> whites = table.getView(Stone.WHITE);
		ColorView<Integer, ?> blacks = table.getView(Stone.BLACK);

		assert null != table.put(Stone.WHITE, 1);
		assert whites.getFields().equals(_makeSet(1));
		assert blacks.getFields().equals(_makeSet());

		assert null != table.put(Stone.WHITE, 2);
		assert whites.getFields().equals(_makeSet(1, 2));
		assert blacks.getFields().equals(_makeSet());

		assert null != table.put(Stone.BLACK, 0);
		assert whites.getFields().equals(_makeSet(1, 2));
		assert blacks.getFields().equals(_makeSet(0));

		assert null != table.put(Stone.BLACK, 3);
		assert whites.getFields().equals(_makeSet());
		assert blacks.getFields().equals(_makeSet(0, 3));

		assert null != table.put(Stone.WHITE, 2);
		assert whites.getFields().equals(_makeSet(2));
		assert blacks.getFields().equals(_makeSet(0, 3));

		assert null != table.put(Stone.WHITE, 4);
		assert whites.getFields().equals(_makeSet(2, 4));
		assert blacks.getFields().equals(_makeSet(0));
	}

	@SafeVarargs
	private final <F> HashSet<F> _makeSet(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
