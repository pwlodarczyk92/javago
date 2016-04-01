package bot.Board;

import bot.Bot;
import bot.Score;
import bot.algos.Immortal;
import core.board.StandardBoard;
import core.color.ColorView;
import core.primitives.Stone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ScoredView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxus on 31.03.16.
 */
public class BottedBoard extends StandardBoard implements ScoredView<Integer, Score> {

	final private static Logger logger = LoggerFactory.getLogger(BottedBoard.class.getName());
	private final Bot<Integer> bot;

	public BottedBoard() {
		super();
		this.bot = new Bot<>(board, makeboard());
	}

	public void dobotmove() {
		bot.makemove();
	}

	public HashMap<Integer, Double> immortals() {

		ColorView<Integer, Integer> whites = board.tableview().getview(Stone.WHITE);
		ColorView<Integer, Integer> blacks = board.tableview().getview(Stone.BLACK);
		Immortal.Result<Integer, Integer> wr = Immortal.immortals(board.tableview().getadjacency(), whites);
		Immortal.Result<Integer, Integer> br = Immortal.immortals(board.tableview().getadjacency(), blacks);
		HashMap<Integer, Double> result = new HashMap<>();

		logger.trace("white points taken: {}", wr.points.size());
		logger.trace("black points taken: {}", br.points.size());
		for (Integer f: wr.points)
			result.put(f, 1.0);
		for (Integer f: br.points)
			result.put(f, -1.0);
		for (Integer g: wr.groups)
			whites.getnodes(g).forEach(f -> result.put(f, 1.0));
		for (Integer g: br.groups)
			blacks.getnodes(g).forEach(f -> result.put(f, -1.0));
		return result;

	}

	@Override
	public Map<Integer, Double> getscores(Score score) {
		switch (score) {
			case CONTROL:
				return bot.control();
			case SCORE:
				return bot.score();
			case IMMORTAL:
				return immortals();
			default: throw new RuntimeException();
		}
	}

}
