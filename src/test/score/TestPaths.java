package score;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by maxus on 24.04.16.
 */
public class TestPaths {

	public static class Pos {
		public final int x;
		public final int y;
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static List<Pos> longPath() {
		List<Pos> result = new ArrayList<>();
		BiConsumer<Integer, Integer> add = (x, y) -> result.add(new Pos(x, y));
		add.accept(1, 5);
		add.accept(2, 5);
		add.accept(3, 5);
		add.accept(7, 5);
		add.accept(2, 6);
		add.accept(2, 4);
		add.accept(3, 4);
		add.accept(7, 4);
		add.accept(2, 3);
		add.accept(1, 4);
		add.accept(0, 4);
		add.accept(1, 3);
		add.accept(1, 2);
		add.accept(0, 3);
		add.accept(7, 3);
		return result;
	}


	public static List<Pos> shortPath() {
		List<Pos> result = new ArrayList<>();
		BiConsumer<Integer, Integer> add = (x, y) -> result.add(new Pos(x, y));
		add.accept(2, 4);
		add.accept(3, 4);
		add.accept(4, 4);
		add.accept(8, 4);
		add.accept(3, 3);
		add.accept(3, 5);
		add.accept(2, 5);
		add.accept(8, 5);
		return result;
	}
}
