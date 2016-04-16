package core.board;

import core.color.Color;
import core.table.Table;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public class GameConcrTest extends GameTest<Game<Integer, Integer>> {
	@Override
	protected Game<Integer, Integer> createInstance(
			Collection<Integer> fields,
			Function<Integer, Collection<Integer>> adjacency,
			Color<Integer> whites,
			Color<Integer> blacks) {
		return new Game<>(new Table<>(fields, adjacency, whites, blacks));
	}
}
