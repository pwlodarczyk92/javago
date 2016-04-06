package core;

import core.board.Board;
import core.color.IntColor;
import core.primitives.MoveNotAllowed;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public abstract class BoardTest<B extends Board<Integer, ?>> {

	private static class LinAdj implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adj = new LinAdj();

	protected abstract B createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks);

	@org.junit.Test
	public final void simpleTest() {
		B oldboard = createInstance(adj, new IntColor(adj), new IntColor(adj));
		B board = createInstance(adj, new IntColor(adj), new IntColor(adj));

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
		assert board.getview().equals(oldboard.getview());
	}

}
