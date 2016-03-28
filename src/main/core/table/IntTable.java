package core.table;

import core.color.IntColor;
import utils.Copyable;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 29.03.16.
 */
public class IntTable extends Table<Integer, Integer, IntColor> implements Copyable<IntTable>{
	public IntTable(Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		super(adjacency, whites, blacks);
	}
	@Override
	public IntTable copy() {
		return new IntTable(adjacency, whites.copy(), blacks.copy());
	}
}
