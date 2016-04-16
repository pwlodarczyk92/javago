package algos.tree.helpers;

import algos.tree.structure.Node;
import algos.tree.structure.TreeGame;
import core.Stone;
import core.board.IState;
import core.table.TableView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maxus on 12.04.16.
 */
public class AtariExpander {
	public static <F, G> Set<Node<F, IState<F, G>>> expand(TreeGame<F, G> tree, Node<F, IState<F, G>> node) {

		Set<F> ataris = new HashSet<>();
		Set<Node<F, IState<F, G>>> result = new HashSet<>();
		TableView<F, G> view = node.state.getTable();
		Stone now = node.state.getCurrentStone();

		view.getView(now).getGroups().stream().forEach(g -> {
			Set<F> libs = view.getLiberties(now, g);
			if (libs.size() == 1) ataris.addAll(libs);
		});

		view.getView(now.opposite()).getGroups().stream().forEach(g -> {
			Set<F> libs = view.getLiberties(now.opposite(), g);
			if (libs.size() <= 2) {
				ataris.addAll(libs);
			}
		});

		for (F field: ataris) {
			Node<F, IState<F, G>> newnode = tree.makeMove(node, field);
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