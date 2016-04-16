package algos.tree.helpers;

import algos.tree.structure.Node;
import algos.tree.structure.TreeGame;
import core.board.IState;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maxus on 12.04.16.
 */
public class AdjExpander {

	public static <F, G> Set<Node<F, IState<F, G>>> expand(TreeGame<F, G> tree, Node<F, IState<F, G>> node) {

		Set<Node<F, IState<F, G>>> result = new HashSet<>();
		for (F field: node.state.getTable().getMooreLiberties()) {
			Node<F, IState<F, G>> newNode = tree.makeMove(node, field);
			if (newNode != null) result.add(newNode);
		}
		return result;

	}

	public static <F, G> void expandbulk(TreeGame<F, G> tree, Node<F, IState<F, G>> root, int iterations) {
		HashSet<Node<F, IState<F, G>>> nodes = new HashSet<>();
		nodes.add(root);
		while(iterations>=1) {
			iterations -= 1;
			HashSet<Node<F, IState<F, G>>> newNodes = new HashSet<>();
			for (Node<F, IState<F, G>> node: nodes)
				newNodes.addAll(expand(tree, node));
			nodes = newNodes;
		}
	}
}
