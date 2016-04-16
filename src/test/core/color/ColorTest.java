package core.color;

import java.util.Collection;
import java.util.function.Function;


public class ColorTest extends IntColorTest<Integer> {

	@Override
	protected IColor<Integer, Integer> createInstance(Function<Integer, Collection<Integer>> adjacency) {
		return new Color<>(adjacency);
	}

}