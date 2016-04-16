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
		for (F field: node.state.gettable().getmoorelibs()) {
			Node<F, IState<F, G>> newnode = tree.moveto(node, field);
			if (newnode != null) result.add(newnode);
		}
		return result;

	}

	public static <F, G> void expandbulk(TreeGame<F, G> tree, Node<F, IState<F, G>> root, int iter) {
		HashSet<Node<F, IState<F, G>>> nodes = new HashSet<>();
		nodes.add(root);
		while(iter>=1) {
			iter -= 1;
			HashSet<Node<F, IState<F, G>>> newnodes = new HashSet<>();
			for (Node<F, IState<F, G>> node: nodes)
				newnodes.addAll(expand(tree, node));
			nodes = newnodes;
		}
	}
}
