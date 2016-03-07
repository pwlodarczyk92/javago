package core;

import core.primitives.Stone;
import core.table.Table;
import utils.Copyable;
import core.table.color.IColor;
import core.table.color.IntColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Created by maxus on 03.03.16.
 */
public abstract class TableTest<G, C extends IColor<Integer,G> & Copyable<C>, T extends Table<Integer, G, C>> {

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinAdj();

	protected abstract T createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks);

	@org.junit.Test
	public final void simpleTest() {
		T table = createInstance(adj, new IntColor(adj), new IntColor(adj));
		C w = table.getWhites();
		C b = table.getBlacks();

		table.put(Stone.WHITE, 1);
		assert w.allstones().equals(mkset(1));
		assert b.allstones().equals(mkset());

		table.put(Stone.WHITE, 2);
		assert w.allstones().equals(mkset(1, 2));
		assert b.allstones().equals(mkset());

		table.put(Stone.BLACK, 0);
		assert w.allstones().equals(mkset(1, 2));
		assert b.allstones().equals(mkset(0));

		table.put(Stone.BLACK, 3);
		assert w.allstones().equals(mkset());
		assert b.allstones().equals(mkset(0, 3));

		table.put(Stone.WHITE, 2);
		assert w.allstones().equals(mkset(2));
		assert b.allstones().equals(mkset(0, 3));

		table.put(Stone.WHITE, 4);
		assert w.allstones().equals(mkset(2, 4));
		assert b.allstones().equals(mkset(0));
	}

	@SafeVarargs
	private final <F> HashSet<F> mkset(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
