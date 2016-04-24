package algos.atari;

import algos.tree.helpers.Descriptor;
import algos.tree.Node;
import core.Stone;
import core.board.IState;
import core.color.ColorView;
import core.table.TableView;

import java.util.*;


/**
 * Created by maxus on 21.04.16.
 */
class PathsDescriptor<F, G> extends Descriptor<F, IState<F, G>, Integer> {

	private HashMap<Node<F, IState<F, G>>, Set<F>> options = new HashMap<>();
	private final F field;
	private final Stone defender;
	private final Stone attacker;

	public PathsDescriptor(F field, Stone stone) {
		this.field = field;
		this.defender = stone;
		this.attacker = stone.opposite();
	}

	@Override
	public Integer rawScore(Node<F, IState<F, G>> newNode) {

		TableView<F, G> view = newNode.state.getTable();
		if (view.getStone(field) == Stone.EMPTY) return 0; // killed

		G defendingGroup = view.getView(defender).getGroup(field);
		boolean attacking = newNode.state.getCurrentStone() == attacker;
		Set<F> newOptions = new HashSet<>(view.getLiberties(defender, defendingGroup));
		if (newOptions.size() > 2 || newOptions.size() == 2 && !attacking) return Integer.MAX_VALUE; // saved

		if (!attacking) {
			ColorView<F, G> attacker = view.getView(this.attacker);
			ColorView<F, G> defender = view.getView(this.defender);
			defender.getAdjacents(defender.getGroup(this.field))
					.stream()
					.filter(adjacent -> view.getStone(adjacent) == this.attacker)
					.map(attacker::getGroup)
					.distinct()
					.map(group -> view.getLiberties(this.attacker, group))
					.filter(liberties -> liberties.size() == 1)
					.forEach(newOptions::addAll);
		}

		this.options.put(newNode, newOptions);

		if (attacking) return 1;
		else return newOptions.size();

	}

	@Override
	public Integer treeScore(Collection<Integer> pathNumber, Node<F, IState<F, G>> node) {
		if (defender == node.state.getCurrentStone()) // defending move
			return options.get(node).size() + pathNumber.stream().mapToInt(i -> i).sum();
		else {
			int result = options.get(node).isEmpty() ? Integer.MAX_VALUE : 1;
			if (!pathNumber.isEmpty()) result = Math.min(result, Collections.min(pathNumber));
			return result;
		}
	}

	public boolean hasOption(Node<F, IState<F, G>> node) {
		return !options.get(node).isEmpty();
	}

	public F getOption(Node<F, IState<F, G>> node) {
		Iterator<F> i = options.get(node).iterator();
		F result = i.next();
		i.remove();
		return result;
	}

}
