package bot;

import bot.algos.DummyScore;
import bot.algos.Control;
import core.board.Board;
import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by maxus on 30.03.16.
 */
public class Bot<F> {

	final private static Logger logger = LoggerFactory.getLogger(Bot.class.getName());

	private final Board<F, ?> board;
	private final Board<F, ?> internalboard;

	public Bot(Board<F, ?> board, Board<F, ?> emptyboard) {
		this.board = board;
		this.internalboard = emptyboard;
	}

	private void alignBoard() {

		List<F> internalmoves = internalboard.moves();
		List<F> boardmoves = board.moves();
		Iterator<F> iiter = internalmoves.iterator();
		Iterator<F> biter = boardmoves.iterator();

		int samemovenum = 0;
		F different = null;

		while (iiter.hasNext() && biter.hasNext()) {
			F imove = iiter.next();
			F bmove = biter.next();
			if (!imove.equals(bmove)) {
				different = bmove;
				break;
			}
			samemovenum+=1;
		}

		int delnum = internalmoves.size() - samemovenum;
		for (int i = delnum; i>0; i--)
			internalboard.undo(); //remove wrong moves
		if(different != null)
			internalboard.put(different); //make last misaligned move
		while (biter.hasNext())
			internalboard.put(biter.next()); //make rest of the unchecked board moves
		assert internalmoves.equals(boardmoves);

	}

	public HashMap<F, Double> score() {
		alignBoard();
		return DummyScore.get(internalboard);
	}

	public HashMap<F, Double> control() {
		return Control.get(board.tableview());
	}

	public void makemove() {

		alignBoard();
		Stone stone = internalboard.currentstone();
		Double factor = (stone == Stone.WHITE ? 1.0 : -1.0);

		ArrayList<F> bestmoves = new ArrayList<>();
		Double bestpoints = Double.MIN_VALUE;
		HashMap<F, Double> scores = DummyScore.get(internalboard);

		for(F field: scores.keySet()) {
			Double score = factor * scores.get(field);
			if (bestpoints<score) {
				bestmoves.clear();
				bestpoints = score;
			}
			if (bestpoints.equals(score))
				bestmoves.add(field);
		}

		if (bestmoves.isEmpty()) {
			throw new MoveNotAllowed();
		}
		board.put(bestmoves.get(0));

	}


}
