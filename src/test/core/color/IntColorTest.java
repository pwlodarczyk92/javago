package core.color;

import com.google.common.collect.Collections2;
import org.junit.Before;

import java.util.*;
import java.util.function.Function;

/**
 * Created by maxus on 21.02.16.
 */
public abstract class IntColorTest<G>{

	private static class LinearAdjacency implements Function<Integer, Collection<Integer>> {
		@Override
		public Collection<Integer> apply(Integer integer) {
			return Arrays.asList(integer - 1, integer + 1);
		}
	}

	private Function<Integer, Collection<Integer>> adjacency = new LinearAdjacency();
	private IColor<Integer, G> instance;

	protected abstract IColor<Integer, G> createInstance(Function<Integer, Collection<Integer>> adjacency);

	@Before
	public final void setUp() {
		this.instance = createInstance(new LinearAdjacency());
	}

	@org.junit.Test
	public final void singletonTest() {

		instance.addStone(3);
		G group = instance.getGroup(3);

		assert instance.getFields().equals(mkSet(3));
		assert instance.getFields(group).equals(mkSet(3));
		assert instance.getAdjacents(group).equals(mkSet(2, 4));
	}

	@org.junit.Test
	public final void singleGroupTest() {

		HashSet<Integer> group = mkSet(3, 4, 5, 6);

		for(List<Integer> permutation: Collections2.permutations(group)) {

			IColor<Integer, G> localInstance = createInstance(adjacency);

			for (Integer i: permutation) {
				localInstance.addStone(i);
			}

			assert localInstance.getFields().equals(group);

			G root1 = localInstance.getGroup(permutation.get(0));
			for (Integer i: permutation) {
				assert localInstance.getGroup(i).equals(root1);
			}

			localInstance.removeGroup(root1);
			assert localInstance.getFields().isEmpty();

		}
	}

	@org.junit.Test
	public final void twoGroupTest() {

		HashSet<Integer> groups = mkSet(3, 4, 7, 6);

		for(List<Integer> perm: Collections2.permutations(groups)) {

			IColor<Integer, G> localInstance = createInstance(adjacency);
			for (Integer i: perm) {
				localInstance.addStone(i);
			}

			assert localInstance.getFields().equals(groups);

			assert Objects.equals(localInstance.getGroup(3), localInstance.getGroup(4));
			assert Objects.equals(localInstance.getGroup(6), localInstance.getGroup(7));
			assert !Objects.equals(localInstance.getGroup(7), localInstance.getGroup(4));
			assert !Objects.equals(localInstance.getGroup(7), localInstance.getGroup(3));
			assert !Objects.equals(localInstance.getGroup(6), localInstance.getGroup(4));
			assert !Objects.equals(localInstance.getGroup(6), localInstance.getGroup(3));

			localInstance.removeGroup(localInstance.getGroup(3));
			assert localInstance.getFields().equals(mkSet(6, 7));

		}
	}

	@org.junit.Test
	public final void forkTest() {

		HashSet<Integer> groups = mkSet(3, 4, 7, 6);

		for(List<Integer> permutation: Collections2.permutations(groups)) {

			IColor<Integer, G> instance = createInstance(adjacency);
			for (Integer i: permutation) {
				instance.addStone(i);
			}

			IColor<Integer, G> instanceFork = instance.fork();

			assert instance.getFields().equals(groups);
			assert instanceFork.getFields().equals(groups);

			assert Objects.equals(instance.getGroup(3), instance.getGroup(4));
			assert Objects.equals(instance.getGroup(6), instance.getGroup(7));
			assert !Objects.equals(instance.getGroup(7), instance.getGroup(4));
			assert !Objects.equals(instance.getGroup(7), instance.getGroup(3));
			assert !Objects.equals(instance.getGroup(6), instance.getGroup(4));
			assert !Objects.equals(instance.getGroup(6), instance.getGroup(3));

			assert Objects.equals(instanceFork.getGroup(3), instanceFork.getGroup(4));
			assert Objects.equals(instanceFork.getGroup(6), instanceFork.getGroup(7));
			assert !Objects.equals(instanceFork.getGroup(7), instanceFork.getGroup(4));
			assert !Objects.equals(instanceFork.getGroup(7), instanceFork.getGroup(3));
			assert !Objects.equals(instanceFork.getGroup(6), instanceFork.getGroup(4));
			assert !Objects.equals(instanceFork.getGroup(6), instanceFork.getGroup(3));

			instance.removeGroup(instance.getGroup(3));
			assert instance.getFields().equals(mkSet(6, 7));
			assert instanceFork.getFields().equals(groups);

		}
	}

	@SafeVarargs
	private final <F> HashSet<F> mkSet(F... elems) {
		return new HashSet<>(Arrays.asList(elems));
	}

}
