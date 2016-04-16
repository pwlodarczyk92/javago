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
		G oldGame = createInstance(range, adj, new Color<>(adj), new Color<>(adj));
		G game = createInstance(range, adj, new Color<>(adj), new Color<>(adj));

		assert null != oldGame.put(1);  //   W
		assert null != oldGame.put(-1); // B W
		assert null != oldGame.put(2);  // B WW
		assert null != oldGame.put(-2); //BB WW
		assert null != oldGame.put(0);  //BBWWW

		assert null != game.put(1);  //   W
		assert null != game.put(-1); // B W
		assert null != game.put(2);  // B WW
		assert null != game.put(-2); //BB WW
		assert null != game.put(0);  //BBWWW

		assert null != game.put(3);  //BBB  B
		assert null != game.put(2);  //BBB WB
		assert null != game.put(5); //BBB WB B
		assert null != game.put(4); //BBB W WB

		assert null == game.put(4); //BBB WB B - repeated

		game.undo(); //BBB WB B
		game.undo(); //BBB WB
		game.undo(); //BBB  B
		game.undo(); //BBWWW

		assert game.getState().equals(oldGame.getState());
	}

}
