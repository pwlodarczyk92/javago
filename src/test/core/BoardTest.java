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

	private static class LinAdj implements Function<java.lang.Integer, Collection<java.lang.Integer>> {
		@Override
		public Collection<java.lang.Integer> apply(java.lang.Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<java.lang.Integer, Collection<java.lang.Integer>> adj = new LinAdj();

	protected abstract B createInstance(Function<java.lang.Integer, Collection<java.lang.Integer>> adjacency, IntColor whites, IntColor blacks);

	@org.junit.Test
	public final void simpleTest() {
		B oldboard = createInstance(adj, new IntColor(adj), new IntColor(adj));
		B board = createInstance(adj, new IntColor(adj), new IntColor(adj));

		oldboard.put(1);  //   W
		oldboard.put(-1); // B W
		oldboard.put(2);  // B WW
		oldboard.put(-2); //BB WW
		oldboard.put(0);  //BBWWW

		board.put(1);  //   W
		board.put(-1); // B W
		board.put(2);  // B WW
		board.put(-2); //BB WW
		board.put(0);  //BBWWW

		board.put(3);  //BBB  B
		board.put(2);  //BBB WB
		board.put(5); //BBB WB B
		board.put(4); //BBB W WB

		boolean fail = false;
		try {
			board.put(4); //BBB WB B - repeated
		} catch (MoveNotAllowed e) {
			fail = true;
		}
		assert fail;

		board.undo(); //BBB WB B
		board.undo(); //BBB WB
		board.undo(); //BBB  B
		board.undo(); //BBWWW
		assert board.tableview().equals(oldboard.tableview());
	}

}
