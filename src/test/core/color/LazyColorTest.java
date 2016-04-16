package core.color;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by maxus on 14.04.16.
 */
public class LazyColorTest extends IntColorTest<Integer> {

	@Override
	protected IColor<Integer, Integer> createInstance(Function<Integer, Collection<Integer>> adjacency) {
		return new LazyColor<>(adjacency);
	}
}
