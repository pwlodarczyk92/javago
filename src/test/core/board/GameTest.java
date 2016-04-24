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
public abstract class GameTest<G extends IGame<Integer, ?>> {

	private static class LinearAdjacency implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinearAdjacency();

	protected abstract G createInstance(Collection<Integer> fields,
										Function<Integer, Collection<Integer>> adjacency,
										Color<Integer> whites,
										Color<Integer> blacks);

	@org.junit.Test
	public final void simpleTest() {
		List<Integer> range = ContiguousSet.create(Range.closed(-20, 20), DiscreteDomain.integers()).asList();
		G straightGame = createInstance(range, adj, new Color<>(adj), new Color<>(adj));
		G undoGame = createInstance(range, adj, new Color<>(adj), new Color<>(adj));

		assert null != straightGame.put(1);  //   W
		assert null != straightGame.put(-1); // B W
		assert null != straightGame.put(2);  // B WW
		assert null != straightGame.put(-2); //BB WW
		assert null != straightGame.put(0);  //BBWWW

		assert null != undoGame.put(1);  //   W
		assert null != undoGame.put(-1); // B W
		assert null != undoGame.put(2);  // B WW
		assert null != undoGame.put(-2); //BB WW
		assert null != undoGame.put(0);  //BBWWW

		assert null != undoGame.put(3);  //BBB  B
		assert null != undoGame.put(2);  //BBB WB
		assert null != undoGame.put(5);  //BBB WB B
		assert null != undoGame.put(4);  //BBB W WB

		assert null == undoGame.put(4); //BBB WB B - repeated

		undoGame.undo(); //BBB WB B
		undoGame.undo(); //BBB WB
		undoGame.undo(); //BBB  B
		undoGame.undo(); //BBWWW

		assert undoGame.getState().equals(straightGame.getState());
	}

}
