package score;

import api.GameExtView;
import api.Snap;
import implementations.ImmuCore;
import implementations.ImmuLazyCore;
import implementations.LazyCore;
import implementations.StandardCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import score.TestPaths.Pos;
import static score.TestPaths.longPath;

/**
 * Created by maxus on 02.04.16.
 */
public class ConsistenceTest {

	public void check(List<GameExtView> boards) {
		for(Score score: Score.values()) {
			for (GameExtView ss1 : boards) {
				Snap snp1 = ss1.getView(score);
				for (GameExtView ss2 : boards) {
					Snap snp2 = ss2.getView(score);
					if (!snp1.equals(snp2)) {
						assert false;
					}
				}
			}
		}
	}

	@org.junit.Test
	public void consistenceCheck() throws InterruptedException {

		List<GameExtView> boards = new ArrayList<>(Arrays.asList(
				new StandardCore(19).makeGameView(),
				new ImmuCore(19).makeGameView(),
				new LazyCore(19).makeGameView(),
				new ImmuLazyCore(19).makeGameView()));

		List<Pos> list = longPath();
		for (Pos pos: list) {
			for (GameExtView sv : boards) {
				sv.put(pos.x, pos.y);
			}
			check(boards);
		}

	}

}
