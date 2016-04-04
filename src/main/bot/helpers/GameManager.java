package bot.helpers;

import core.board.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by maxus on 30.03.16.
 */
public class GameManager<F, G> {

	final private static Logger logger = LoggerFactory.getLogger(GameManager.class.getName());

	private final Game<F, G> board;
	private final Game<F, G> internalboard;

	public GameManager(Game<F, G> board, Game<F, G> emptyboard) {
		this.board = board;
		this.internalboard = emptyboard;
	}

	//--internal modifiable board management--
	private void alignBoard() {

		if (aligned()) return;

		List<F> internalmoves = internalboard.moves();
		List<F> boardmoves = board.moves();

		Iterator<F> iiter = internalmoves.iterator();
		Iterator<F> biter = boardmoves.iterator();

		int samemovenum = 0;
		F different = null;

		while (iiter.hasNext() && biter.hasNext()) {
			F imove = iiter.next();
			F bmove = biter.next();
			if (Objects.equals(imove, bmove)) {
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

		assert aligned(); //this is actually what this method does.

	}

	private boolean aligned() {
		return internalboard.moves().equals(board.moves());
	}

	public Game<F, ?> board() {
		alignBoard();
		return internalboard;
	}


	public void makemove() {
		throw new NotImplementedException();
	/* --- unfinished ---
		Stone stone = board().getview().getcurrentstone();
		Double factor = (stone == Stone.WHITE ? 1.0 : -1.0);

		ArrayList<F> bestmoves = new ArrayList<>();
		Double bestpoints = Double.MIN_VALUE;
		HashMap<F, Double> scores = DummyScore.get(board());
		assert aligned();

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
	   --- unfinished --- */
	}


}
