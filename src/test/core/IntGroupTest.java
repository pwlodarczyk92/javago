package core;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by maxus on 21.02.16.
 */
public abstract class IntGroupTest<G extends IGroups<Integer>>{

	private static class LinAdj implements Function<Integer, Iterable<Integer>> {
		@Override
		public Iterable<Integer> apply(Integer integer) {
			return () -> Arrays.asList(integer - 1, integer + 1).iterator();
		}
	}
	private G instance;

	protected abstract G createInstance(Function<Integer, Iterable<Integer>> adjacency);

	@Before
	public final void setUp() {
		this.instance = createInstance(new LinAdj());
	}

	@org.junit.Test
	public final void emptyTest() {
		assert instance.size() == 0;
	}

	@org.junit.Test
	public final void singletonTest() {
		instance.add(3);
		assert instance.contains(3);
		assert !instance.contains(2);
		assert !instance.contains(1);
	}

}
