package core.table;

import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import utils.Copyable;
import core.color.IColor;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Created by maxus on 23.02.16.
 */
public class Table<F, G, C extends IColor<F, G> & Copyable<C>> implements Copyable<Table<F, G, C>> {

	private Function<F, Collection<F>> adjacency;
	private C whites;
	private C blacks;

	public Table(Function<F, Collection<F>> adjacency, C whites, C blacks) {
		this.adjacency = adjacency;
		this.whites = whites;
		this.blacks = blacks;
	}

	@Override
	public Table<F, G, C> copy() {
		return new Table<>(adjacency, whites.copy(), blacks.copy());
	}

	public void put(Stone stone, F field) {

		C player = stone == Stone.BLACK ? blacks : whites;
		C enemy = stone == Stone.BLACK ? whites : blacks;

		if (player.contains(field) || enemy.contains(field)) {
			throw new MoveNotAllowed();
		}

		Collection<F> adjacent = adjacency.apply(field);
		boolean moveok = false;

		for (F adj : adjacent) {
			boolean playeradj = player.contains(adj);
			boolean enemyadj = enemy.contains(adj);

			if (!enemyadj && !playeradj) moveok = true;

			else if (enemyadj && getlibs(enemy, player, adj).size()==1) {
				moveok = true;
				enemy.remgroup(enemy.getgroup(adj));
			}

		}

		if (!moveok) {
			for (F adj : adjacent) {
				if (player.contains(adj) && getlibs(player, enemy, adj).size()>1) {
					moveok = true;
					break;
					//TODO: add tests for correctness of this check
					//TODO: older check threw exception if getlibs().size()==1, which is wrong
				}
			}
		}

		if (!moveok) throw new MoveNotAllowed();

		player.addstone(field);

	}

	private Collection<F> getlibs(C main, C substract, F f) {
		HashSet<F> result = new HashSet<>();
		for (F lib: main.getlibs(main.getgroup(f))) {
			if (!substract.contains(lib))
				result.add(lib);
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Table table = (Table) o;

		if (!blacks.equals(table.blacks)) return false;
		if (!whites.equals(table.whites)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (19*19) * whites.hashCode() + blacks.hashCode();
	}

	public Stone getStone(F field) {
		if (whites.contains(field)) return Stone.WHITE;
		if (blacks.contains(field)) return Stone.BLACK;
		return Stone.EMPTY;
	}

	public C getWhites() {
		return whites;
	}

	public C getBlacks() {
		return blacks;
	}

}
