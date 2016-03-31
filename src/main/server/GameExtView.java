package server;

import core.primitives.Stone;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maxus on 07.03.16.
 */
public interface GameExtView<F> {

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

	public List<List<F>> getfields();
	public Map<F, Stone> getstones();
	public Stone currentstone();
	public Integer passcount();

	public default State state() {
		State result = new State();
		updateState(result);
		return result;
	}

	public default void updateState(State result) {
		Map<F, Stone> stones = getstones();
		result.passes = passcount();
		result.current = currentstone().val;
		result.blackpoints = blackpts();
		result.whitepoints = whitepts();
		result.stones = getfields().stream().map(
				column -> column.stream().map(f -> stones.get(f).val).collect(Collectors.toList())
		).collect(Collectors.toList());

	}

}
