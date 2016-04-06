package bot.helpers;

import core.board.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by maxus on 30.03.16.
 */
public class GameManager<F, G> {

	final private static Logger logger = LoggerFactory.getLogger(GameManager.class.getName());

	private final Game<F, G> board;
	protected final Game<F, G> internalboard;

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

			if (!Objects.equals(imove, bmove)) {
				different = bmove;
				break;
			}
			samemovenum+=1;
		}

		int delnum = internalmoves.size() - samemovenum;
		for (int i = delnum; i>0; i--)
			internalboard.undo(); //remove wrong moves
		if(different != null)
			assert internalboard.put(different) != null; //make last misaligned move
		while (biter.hasNext())
			assert internalboard.put(biter.next()) != null; //make rest of the unchecked board moves

		assert aligned(); //this is actually what this method does.

	}

	private boolean aligned() {
		return internalboard.moves().equals(board.moves());
	}

	public Game<F, ?> board() {
		alignBoard();
		return internalboard;
	}

}
