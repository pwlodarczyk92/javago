package core;

import core.board.Board;
import core.table.Table;
import core.color.IntColor;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public class BoardConcrTest extends BoardTest<Integer, IntColor, Board<Integer, Integer, IntColor>> {
	@Override
	protected Board<Integer, Integer, IntColor> createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		return new Board<>(new Table<>(adjacency, whites, blacks));
	}
}
