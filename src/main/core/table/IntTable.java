package core.table;

import core.color.IntColor;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 29.03.16.
 */
public class IntTable extends Table<Integer, Integer> {
	public IntTable(Collection<Integer> fields, Function<Integer, Collection<Integer>> adjacency, IntColor whites, IntColor blacks) {
		super(fields, adjacency, whites, blacks);
	}
	@Override
	public IntTable copy() {
		IntColor wc = (IntColor) whites.copy();
		IntColor bc = (IntColor) blacks.copy();
		return new IntTable(fields, adjacency, wc, bc);
	}

}
