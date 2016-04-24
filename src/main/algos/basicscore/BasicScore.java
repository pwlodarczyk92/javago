package algos.basicscore;

import algos.tree.helpers.Scorer;
import algos.tree.helpers.Utils;
import algos.tree.Node;
import algos.tree.TreeGame;
import core.board.IGame;
import core.board.IState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Created by maxus on 11.04.16.
 */
public class BasicScore {

	final static protected Logger logger = LoggerFactory.getLogger(BasicScore.class.getName());


	public static <F> Map<F, Double> score(IGame<F, ?> board) {
		return _score(board);
	}

	private static <F, G> Map<F, Double> _score(IGame<F, G> board) {

		TreeGame<F, G> tree = new TreeGame<>(board);
		Scorer<F, G> scoring = new Scorer<>(n -> Basic.scoreFunction(n.state));
		tree.register(scoring);

		Set<Node<F, IState<F, G>>> nodes = Utils.mooreExpander(tree).apply(tree.root);
		Node<F, IState<F, G>> zeroNode = tree.makeMove(tree.root, null);

		Function<Node<F, IState<F, G>>, Set<Node<F, IState<F, G>>>> expander = Utils.libertyExpander(tree, 2);
		nodes.forEach(expander::apply);
		expander.apply(zeroNode);

		Map<F, Double> result = nodes.stream().collect(toMap(n -> n.lastMove, scoring));
		Double zeroScore = scoring.apply(zeroNode);

		logger.info("current score: {}", Basic.scoreFunction(board.getState()));
		logger.info("zero score   : {}", zeroScore);
		logger.info("best score   : {}", scoring.apply(tree.root));

		CollectionUtils.normalize(result, zeroScore, false);
		return result;

	}

}
