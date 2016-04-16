package algos.tree.helpers;

import algos.tree.structure.Node;
import algos.tree.structure.TreeGame;
import core.board.IState;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by maxus on 15.04.16.
 */
public class ScoreExpander {

	public static <F, G> HashMap<F, Double> expand(TreeGame<F, G> tb, BiConsumer<TreeGame<F, G>, Node<F, IState<F, G>>> expander) {

		HashMap<F, Double> result = new HashMap<>();

		Node<F, IState<F, G>> zeronode = tb.moveto(tb.root, null);
		if (zeronode!=null) expander.accept(tb, zeronode);
		else return new HashMap<>();

		for(F field : tb.root.state.gettable().getmoorelibs()) {
			Node<F, IState<F, G>> movenode = tb.moveto(tb.root, field);
			if (movenode!=null) expander.accept(tb, movenode);
		}

		result.put(null, zeronode.score());
		for (F field : tb.root.state.gettable().getmoorelibs()) {
			Node<F, IState<F, G>> movenode = tb.moveto(tb.root, field);
			if (movenode != null) result.put(field, movenode.score());
		}

		return result;

	}
}
