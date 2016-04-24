package algos.tree.helpers;

import algos.tree.Node;
import algos.tree.TreeGame;
import core.board.IState;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Created by maxus on 18.04.16.
 */
public class Utils {


	public static <F, G> Function<Node<F, IState<F, G>>, Collection<F>> mooreOptions() {

		return node -> node.state.getTable().getMooreLiberties();

	}

	public static <F, G> Function<Node<F, IState<F, G>>, Collection<F>> libertyOptions() {

		return node -> node.state.getTable().getAllLiberties();

	}

	public static <F, G> Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> optionsExpander(
			Function<Node<F, IState<F, G>>, Collection<F>> options,
			TreeGame<F, G> game) {

		return node -> options.apply(node).stream()
				.map(f -> game.makeMove(node, f))
				.filter(x -> x != null)
				.collect(toSet());

	}

	public static <F, G> Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> mooreExpander(
			TreeGame<F, G> game) {

		return optionsExpander(mooreOptions(), game);

	}

	public static <F, G> Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> libertyExpander(
			TreeGame<F, G> game,
			int iterations) {

		return multiplyExpander(optionsExpander(libertyOptions(), game), iterations);

	}


	public static <F, G> Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> multiplyExpander(
			Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> selector,
			int iterations) {

		if (iterations == 1) return selector;

		return node -> {

			Stream<Node<F, IState<F, G>>> result = selector.apply(node).stream();
			int applicationsLeft = iterations-1;

			while (applicationsLeft > 0) {
				applicationsLeft-=1;
				result = result.flatMap(nextnode -> selector.apply(nextnode).stream());
			}

			return result.collect(toSet());

		};

	}

}
