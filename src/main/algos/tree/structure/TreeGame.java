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

	private IGame<F, G> rootGame;

	public final Node<F, IState<F, G>> root;
	public final Function<IState<F, G>, Double> scorer;

	public TreeGame(IGame<F, G> rootGame, Function<IState<F, G>, Double> scorer) {
		this.rootGame = rootGame;
		this.scorer = scorer;
		this.root = _makeRoot(rootGame, scorer);
	}

	private static <F, G> Node<F, IState<F, G>> _makeRoot(IGame<F, G> rootGame, Function<IState<F, G>, Double> scorer) {
		IState<F, G> rootState = rootGame.getState();
		return new Node<>(rootState, scorer.apply(rootState), rootState.getCurrentStone() == Stone.WHITE);
	}

	//--accessors--
	public Node<F, IState<F, G>> makeMove(Node<F, IState<F, G>> node, F field) {

		if (node.successors.containsKey(field)) //try to use already existing node
			return node.successors.get(field);

		Map.Entry<? extends Set<F>, ? extends IState<F, G>> newData = node.state.put(field); //make move, check if it's valid
		if (newData == null) {
			node.setInvalid(field);
			return null;
		}

		IState<F, G> newState = newData.getValue();

		if (field != null) { // check super-ko violation
			
			if (rootGame.isKoViolatedBy(newState)) { //tail part check of ko violation
				node.setInvalid(field);
				return null;
			}

			Node<F, IState<F, G>> nodeBefore = node;  //tree part check of ko violation
			while (nodeBefore != root) {
				IState<F, G> stateBefore = nodeBefore.state;
				if (stateBefore.hashCode() == newState.hashCode() && stateBefore.equals(newState)) {
					node.setInvalid(field);
					return null;
				}
				nodeBefore = nodeBefore.predecessor;
			}
		}

		return node.expand(newState, field, scorer.apply(newState));

	}

}
