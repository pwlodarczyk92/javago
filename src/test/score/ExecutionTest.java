package score;

import api.GameExtView;
import implementations.ImmuLazyCore;

import java.util.List;

import static score.TestPaths.shortPath;
import static score.TestPaths.longPath;

/**
 * Created by maxus on 23.04.16.
 */
public class ExecutionTest {

	@org.junit.Test
	public void runtest() throws InterruptedException {

		GameExtView board = new ImmuLazyCore(19).makeGameView();

		List<TestPaths.Pos> list = shortPath();
		for (TestPaths.Pos pos: list) {
			board.put(pos.x, pos.y);
			for (Score score: Score.values())
				board.getView(score);
		}

		board = new ImmuLazyCore(19).makeGameView();

		list = longPath();
		for (TestPaths.Pos pos: list) {
			board.put(pos.x, pos.y);
			for (Score score: Score.values())
				board.getView(score);
		}

	}

}
