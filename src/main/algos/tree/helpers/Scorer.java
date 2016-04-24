package algos.tree.helpers;

import algos.tree.Node;
import core.Stone;
import core.board.IState;

import java.util.Collection;
import java.util.function.Function;

import static java.util.Collections.max;
import static java.util.Collections.min;

/**
 * Created by maxus on 21.04.16.
 */
public class Scorer<F, G> extends Descriptor<F, IState<F, G>, Double> {

	private Function<Node<F, IState<F, G>>, Double> rawScorer;

	public Scorer(Function<Node<F, IState<F, G>>, Double> rawScorer) {
		super();
		this.rawScorer = rawScorer;
	}

	@Override
	public Double rawScore(Node<F, IState<F, G>> newNode) {
		return rawScorer.apply(newNode);
	}

	@Override
	public Double treeScore(Collection<Double> scores, Node<F, IState<F, G>> node) {
		return node.state.getCurrentStone() == Stone.WHITE ? max(scores) : min(scores);
	}
}
