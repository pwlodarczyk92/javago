package core;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import core.board.IntBoard;
import core.color.IntColor;
import core.table.IntTable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by maxus on 04.03.16.
 */
public class BoardConcrTest extends BoardTest<IntBoard> {
	@Override
	protected IntBoard createInstance(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		List<Integer> ran = ContiguousSet.create(Range.closed(-20, 20), DiscreteDomain.integers()).asList();
		return new IntBoard(new IntTable(ran, adjacency, whites, blacks));
	}
}
