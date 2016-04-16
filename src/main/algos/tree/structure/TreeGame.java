package algos.tree.structure;

import core.Stone;
import core.board.IGame;
import core.board.IState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by maxus on 08.04.16.
 */
public class TreeGame<F, G> {

	final protected Logger logger = LoggerFactory.getLogger(TreeGame.class.getName());

	private IGame<F, G> tailboard;

	public final Node<F, IState<F, G>> root;
	public final Function<IState<F, G>, Double> scorer;

	public TreeGame(IGame<F, G> tailboard, Function<IState<F, G>, Double> scorer) {
		this.tailboard = tailboard;
		this.scorer = scorer;
		this.root = makeroot(tailboard, scorer);
	}

	private static <F, G> Node<F, IState<F, G>> makeroot(IGame<F, G> tailboard, Function<IState<F, G>, Double> scorer) {
		IState<F, G> currentstate = tailboard.getstate();
		return new Node<>(currentstate, scorer.apply(currentstate), currentstate.getcurrentstone() == Stone.WHITE);
	}

	//--accessors--
	public Node<F, IState<F, G>> moveto(Node<F, IState<F, G>> node, F field) {

		if (node.successors.containsKey(field)) //try to use already existing node
			return node.successors.get(field);

		Map.Entry<? extends Set<F>, ? extends IState<F, G>> newdata = node.state.put(field); //make move, check if it's valid
		if (newdata == null) {
			node.setinvalid(field);
			return null;
		}

		IState<F, G> newstate = newdata.getValue();

		if (field != null) { // check super-ko violation
			if (tailboard.superkoviolation(newstate)) { //tail part check of superko
				node.setinvalid(field);
				return null;
			}

			Node<F, IState<F, G>> previous = node;  //tree part check of superko
			while (previous != root) {
				IState<F, G> prevstate = previous.state;
				if (prevstate.hashCode() == newstate.hashCode() && prevstate.equals(newstate)) {
					node.setinvalid(field);
					return null;
				}
				previous = previous.predecessor;
			}
		}

		Node<F, IState<F, G>> newnode = node.expand(newstate, field, scorer.apply(newstate));
		return newnode;

	}

}
