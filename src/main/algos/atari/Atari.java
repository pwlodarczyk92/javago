package algos.atari;

import algos.tree.Node;
import algos.tree.TreeGame;
import core.Stone;
import core.board.IGame;
import core.board.IState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by maxus on 21.04.16.
 */
public class Atari {

	private static final Logger logger = LoggerFactory.getLogger(Atari.class);
	private static boolean notFinished(Integer i) {
		return i > 0 && Integer.MAX_VALUE > i;
	}

	public static <F, G> boolean isAtari(IGame<F, G> game, F field) {

		int count = 0;
		Stone stone = game.getState().getTable().getStone(field);

		TreeGame<F, G> tree = new TreeGame<>(game);
		PathsDescriptor<F, G> paths = paths(tree.root, field);
		tree.register(paths);

		List<Node<F, IState<F, G>>> nodes = new ArrayList<>();
		Node<F, IState<F, G>> node;

		while (notFinished(paths.apply(tree.root))) {

			node = nodes.isEmpty() ? tree.root : nodes.remove(nodes.size()-1);

			if (paths.hasOption(node)) { //expand leaf, stop exploring atari right now
				while (paths.hasOption(node)) {
					tree.makeMove(node, paths.getOption(node));
					count += 1;
				}

			} else if (stone == node.state.getCurrentStone()) { // explore all options when defender is moving
				for (Node<F, IState<F, G>> nextNode: node.successors.values())
					if (nextNode != null && notFinished(paths.apply(nextNode)))
						nodes.add(nextNode);

			} else {	// check best option when attacker is moving
				Node<F, IState<F, G>> bestNode = null;
				Integer bestScore = Integer.MAX_VALUE;

				for (Node<F, IState<F, G>> nextNode: node.successors.values()) {
					Integer nextScore = paths.apply(nextNode);
					if (nextNode != null && nextScore < bestScore) {
						bestNode = nextNode;
						bestScore = nextScore;
					}
				}

				if (bestNode == null) throw new RuntimeException();
				nodes.add(bestNode);
			}

		}

		logger.info("expanded nodes count: {}", count);
		return paths.apply(tree.root) == 0;
	}

	private static <F, G> PathsDescriptor<F, G> paths(Node<F, IState<F, G>> node, F field) {
		return new PathsDescriptor<>(field, node.state.getTable().getStone(field));
	}
}
