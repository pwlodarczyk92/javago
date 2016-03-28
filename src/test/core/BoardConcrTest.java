package core;

import core.board.Board;
import core.board.IntBoard;
import core.table.IntTable;
import core.table.Table;
import core.color.IntColor;
import core.table.TableView;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public class BoardConcrTest extends BoardTest<IntBoard> {
	@Override
	protected IntBoard createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		return new IntBoard(new IntTable(adjacency, whites, blacks));
	}
}
