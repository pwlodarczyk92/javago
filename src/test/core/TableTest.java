package core;

import core.color.ColorView;
import core.color.IntColor;
import core.primitives.Stone;
import core.table.Table;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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

	private Function<Integer, Collection<Integer>> adj = new LinAdj();

	protected abstract Table<Integer, ?> createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks);

	@org.junit.Test
	public final void simpleTest() {
		Table<Integer, ?> table = createInstance(adj, new IntColor(adj), new IntColor(adj));
		ColorView<Integer, ?> w = table.getview(Stone.WHITE);
		ColorView<Integer, ?> b = table.getview(Stone.BLACK);

		assert null != table.put(Stone.WHITE, 1);
		assert w.getallnodes().equals(mkset(1));
		assert b.getallnodes().equals(mkset());

		assert null != table.put(Stone.WHITE, 2);
		assert w.getallnodes().equals(mkset(1, 2));
		assert b.getallnodes().equals(mkset());

		assert null != table.put(Stone.BLACK, 0);
		assert w.getallnodes().equals(mkset(1, 2));
		assert b.getallnodes().equals(mkset(0));

		assert null != table.put(Stone.BLACK, 3);
		assert w.getallnodes().equals(mkset());
		assert b.getallnodes().equals(mkset(0, 3));

		assert null != table.put(Stone.WHITE, 2);
		assert w.getallnodes().equals(mkset(2));
		assert b.getallnodes().equals(mkset(0, 3));

		assert null != table.put(Stone.WHITE, 4);
		assert w.getallnodes().equals(mkset(2, 4));
		assert b.getallnodes().equals(mkset(0));
	}

	@SafeVarargs
	private final <F> HashSet<F> mkset(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
