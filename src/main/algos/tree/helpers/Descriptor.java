package algos.tree.helpers;

import algos.tree.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by maxus on 20.04.16.
 */
public abstract class Descriptor<F, S, D> implements BiConsumer<Node<F, S>, Boolean>, Function<Node<F, S>, D> {

	private final HashMap<Node<F, S>, D> scoreMap = new HashMap<>();
	private final boolean filter;

	public abstract D rawScore(Node<F, S> newNode);
	public abstract D treeScore(Collection<D> successorDescriptions, Node<F, S> node);

	@Override
	public void accept(Node<F, S> node, Boolean nulled) {

		if (!nulled) {
			scoreMap.put(node, rawScore(node));
			node = node.predecessor;
		}

		Collection<D> scores;
		D newScore;
		D oldScore;

		while (node != null) {

			oldScore = scoreMap.get(node);
			Collection<Node<F, S>> nodes = node.successors.values();
			Stream<Node<F, S>> stream = nodes.stream();
			if (filter) stream = stream.filter(i -> i!=null);
			scores = stream.map(scoreMap::get).collect(toList());
			newScore = treeScore(scores, node);

			if (Objects.equals(newScore, oldScore)) return;
			scoreMap.put(node, newScore);

			node = node.predecessor;
		}
	}

	@Override
	public D apply(Node<F, S> fsNode) {
		return scoreMap.get(fsNode);
	}

	public Descriptor(D nullValue) {
		scoreMap.put(null, nullValue);
		this.filter = false;
	}

	public Descriptor() {
		this.filter = true;
	}

}
