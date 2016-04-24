package algos.tree;

import core.board.IGame;
import core.board.IState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by maxus on 08.04.16.
 */
public class TreeGame<F, G> {

	final protected Logger logger = LoggerFactory.getLogger(TreeGame.class.getName());

	private final IGame<F, G> rootGame;
	public final Node<F, IState<F, G>> root;

	private final List<BiConsumer<Node<F, IState<F, G>>, Boolean>> observers;
	private boolean initiated;

	public TreeGame(IGame<F, G> rootGame) {
		this.rootGame = rootGame;
		this.root = new Node<>(rootGame.getState());

		this.observers = new ArrayList<>();
		this.initiated = false;
	}

	public void register(BiConsumer<Node<F, IState<F, G>>, Boolean> observer) {
		if (initiated) throw new RuntimeException();
		this.observers.add(observer);
		observer.accept(this.root, false);
	}

	//--accessors--
	public Node<F, IState<F, G>> makeMove(Node<F, IState<F, G>> node, F field) {

		if (!initiated) initiated = true;

		if (node.successors.containsKey(field)) //try to use already existing node
			return node.successors.get(field);

		Map.Entry<? extends Set<F>, ? extends IState<F, G>> newData = node.state.put(field); // try to make move
		if (newData == null) return _invalidMove(node, field);

		IState<F, G> newState = newData.getValue();

		if (field != null) {
			if (rootGame.isKoViolatedBy(newState))
				return _invalidMove(node, field);

			Node<F, IState<F, G>> nodeBefore = node;  //tree part check of ko violation
			while (nodeBefore != root) {
				IState<F, G> stateBefore = nodeBefore.state;

				if (stateBefore.hashCode() == newState.hashCode() && stateBefore.equals(newState))
					return _invalidMove(node, field);

				nodeBefore = nodeBefore.predecessor;
			}
		}

		Node<F, IState<F, G>> newNode = node.expand(newState, field, newData.getKey());

		for (BiConsumer<Node<F, IState<F, G>>, Boolean> observer: this.observers)
			observer.accept(newNode, false);

		return newNode;
	}

	private Node<F, IState<F, G>> _invalidMove(Node<F, IState<F, G>> node, F move) {

		node.setInvalid(move);

		for (BiConsumer<Node<F, IState<F, G>>, Boolean> observer: this.observers)
			observer.accept(node, true);

		return null;
	}

	public NodeGame<F, G> makeGame(Node<F, IState<F, G>> node) {
		return new NodeGame<>(this, this.rootGame, node);
	}

}
