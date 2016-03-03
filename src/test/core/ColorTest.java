package core;

import core.table.color.IntColor;

import java.util.Collection;
import java.util.function.Function;


public class ColorTest extends IntColorTest<Integer, IntColor> {

	@Override
	protected IntColor createInstance(Function<Integer, Collection<Integer>> adjacency) {
		return new IntColor(adjacency);
	}

}