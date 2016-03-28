package game;

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
	}

	public void put(int x, int y);
	public void pass();
	public void undo();

	public List<List<Stone>> get();
	public Stone current();
	public Integer passes();

	public default State state() {
		State result = new State();
		result.passes = passes();
		result.current = current().val;
		result.stones = get().stream().map(
				column -> column.stream().map(s -> s.val).collect(Collectors.toList()))
				.collect(Collectors.toList());
		return result;
	}
}
