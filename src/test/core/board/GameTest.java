package core.board;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import core.color.Color;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public abstract class GameTest<B extends IGame<Integer, ?>> {

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinAdj();

	protected abstract B createInstance(Collection<Integer> fields,
										Function<Integer, Collection<Integer>> adjacency,
										Color<Integer> whites,
										Color<Integer> blacks);

	@org.junit.Test
	public final void simpleTest() {
		List<Integer> range = ContiguousSet.create(Range.closed(-20, 20), DiscreteDomain.integers()).asList();
		B oldboard = createInstance(range, adj, new Color<>(adj), new Color<>(adj));
		B board = createInstance(range, adj, new Color<>(adj), new Color<>(adj));

		assert null != oldboard.put(1);  //   W
		assert null != oldboard.put(-1); // B W
		assert null != oldboard.put(2);  // B WW
		assert null != oldboard.put(-2); //BB WW
		assert null != oldboard.put(0);  //BBWWW

		assert null != board.put(1);  //   W
		assert null != board.put(-1); // B W
		assert null != board.put(2);  // B WW
		assert null != board.put(-2); //BB WW
		assert null != board.put(0);  //BBWWW

		assert null != board.put(3);  //BBB  B
		assert null != board.put(2);  //BBB WB
		assert null != board.put(5); //BBB WB B
		assert null != board.put(4); //BBB W WB

		assert null == board.put(4); //BBB WB B - repeated


		board.undo(); //BBB WB B
		board.undo(); //BBB WB
		board.undo(); //BBB  B
		board.undo(); //BBWWW
		assert board.getstate().equals(oldboard.getstate());
	}

}
