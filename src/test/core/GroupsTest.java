package core;

import java.util.function.Function;


public class GroupsTest extends IntGroupTest<Groups<Integer>> {

	@Override
	protected Groups<Integer> createInstance(Function<Integer, Iterable<Integer>> adjacency) {
		return new Groups<>(adjacency);
	}

}