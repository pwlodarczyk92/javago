package algos.tree;

import algos.tree.helpers.AdjExpander;
import algos.tree.helpers.ScoreExpander;
import algos.tree.structure.TreeGame;
import core.board.IGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CollectionUtils;

import java.util.HashMap;

/**
 * Created by maxus on 11.04.16.
 */
public class TreeScore {

	final static protected Logger logger = LoggerFactory.getLogger(TreeScore.class.getName());


	public static <F> HashMap<F, Double> score(IGame<F, ?> board) {
		return helper(board);
	}

	private static <F, G> HashMap<F, Double> helper(IGame<F, G> board) {

		TreeGame<F, G> tb = new TreeGame<>(board, TreeImpl::scorefunc);
		HashMap<F, Double> result = ScoreExpander.expand(tb, (tree, node) -> AdjExpander.expandbulk(tree, node, 1));
		Double zeroscore = result.remove(null);

		logger.info("current score: {}", TreeImpl.scorefunc(board.getstate()));
		logger.info("zero score   : {}", zeroscore);
		logger.info("best score   : {}", tb.root.score());

		CollectionUtils.normalize(result, zeroscore, false);
		return result;

	}

}
