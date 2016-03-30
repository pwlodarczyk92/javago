package server;

import core.primitives.Stone;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by maxus on 07.03.16.
 */
public interface GameExtView {

	public static class State {
		public List<List<Integer>> stones;
		public Integer current;
		public Integer passes;
		public Integer whitepoints;
		public Integer blackpoints;
	}

	public void put(int x, int y);
	public void pass();
	public void undo();

	public int blackpts();
	public int whitepts();

	public List<List<Stone>> getstones();
	public Stone current();
	public Integer passcount();

	public default State state() {
		State result = new State();
		result.passes = passcount();
		result.current = current().val;
		result.blackpoints = blackpts();
		result.whitepoints = whitepts();
		result.stones = getstones().stream().map(
				column -> column.stream().map(s -> s.val).collect(Collectors.toList())
				).collect(Collectors.toList());
		return result;
	}
}
