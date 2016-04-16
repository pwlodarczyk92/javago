package core.table;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import core.Stone;
import core.color.Color;
import core.color.ColorView;
import core.table.ITable;

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

	private Function<Integer, Collection<Integer>> adj = new LinAdj();

	protected abstract ITable<Integer, ?> createInstance(Collection<Integer> fields, Function<Integer, Collection<Integer>> adjacency, Color<Integer> whites, Color<Integer> blacks);

	@org.junit.Test
	public final void simpleTest() {
		List<Integer> range = ContiguousSet.create(Range.closed(-20, 20), DiscreteDomain.integers()).asList();
		ITable<Integer, ?> table = createInstance(range, adj, new Color<>(adj), new Color<>(adj));
		ColorView<Integer, ?> w = table.getview(Stone.WHITE);
		ColorView<Integer, ?> b = table.getview(Stone.BLACK);

		assert null != table.put(Stone.WHITE, 1);
		assert w.getallstones().equals(mkset(1));
		assert b.getallstones().equals(mkset());

		assert null != table.put(Stone.WHITE, 2);
		assert w.getallstones().equals(mkset(1, 2));
		assert b.getallstones().equals(mkset());

		assert null != table.put(Stone.BLACK, 0);
		assert w.getallstones().equals(mkset(1, 2));
		assert b.getallstones().equals(mkset(0));

		assert null != table.put(Stone.BLACK, 3);
		assert w.getallstones().equals(mkset());
		assert b.getallstones().equals(mkset(0, 3));

		assert null != table.put(Stone.WHITE, 2);
		assert w.getallstones().equals(mkset(2));
		assert b.getallstones().equals(mkset(0, 3));

		assert null != table.put(Stone.WHITE, 4);
		assert w.getallstones().equals(mkset(2, 4));
		assert b.getallstones().equals(mkset(0));
	}

	@SafeVarargs
	private final <F> HashSet<F> mkset(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
