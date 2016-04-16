package core.table;

import core.Stone;
import core.color.ColorView;
import core.color.IColor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by maxus on 23.02.16.
 */

public class Table<F, G> implements ITable<F, G> {

	protected Collection<F> fields;
	protected Function<F, Collection<F>> adjacency;
	protected IColor<F, G> whites;
	protected IColor<F, G> blacks;

	public Table(Collection<F> fields, Function<F, Collection<F>> adjacency, IColor<F, G> whites, IColor<F, G> blacks) {
		this.fields = fields;
		this.adjacency = adjacency;
		this.whites = whites;
		this.blacks = blacks;
	}

	//--accessors--
	@Override
	public Collection<F> getfields() {
		return fields;
	}
	@Override
	public Collection<F> adjacency(F node) {
		return adjacency.apply(node);
	}

	@Override
	public ColorView<F, G> getview(Stone s) {
		switch (s) {
			case WHITE: return whites;
			case BLACK: return blacks;
			default: throw new RuntimeException();
		}
	}
	@Override
	public Stone getstone(F field) {
		if (whites.contains(field)) return Stone.WHITE;
		if (blacks.contains(field)) return Stone.BLACK;
		return Stone.EMPTY;
	}
	@Override
	public Set<F> getlibs(Stone s, G group) {
		switch (s) {
			case WHITE: return _getlibsbygroup(whites, blacks, group);
			case BLACK: return _getlibsbygroup(blacks, whites, group);
			default: throw new RuntimeException();
		}
	}
	//--accessors--


	private Set<F> _getlibsbygroup(IColor<F, G> main, IColor<F, G> substract, G group) {

		HashSet<F> result = new HashSet<>(4);

		for (F lib: main.getadjacent(group)) {
			if (!substract.contains(lib))
				result.add(lib);
		}

		return result;

	}

	private Set<F> _getlibsbyfield(IColor<F, G> main, IColor<F, G> substract, F field) {

		return _getlibsbygroup(main, substract, main.getgroup(field));

	}


	//--modifiers--
	@Override
	public Set<F> put(Stone stone, F field) {

		HashSet<F> removed_stones = new HashSet<>();
		IColor<F, G> player = stone == Stone.BLACK ? blacks : whites;
		IColor<F, G> enemy = stone == Stone.BLACK ? whites : blacks;

		if (player.contains(field) || enemy.contains(field))
			return null;

		Collection<F> adjacent = adjacency.apply(field);
		boolean moveok = false;

		for (F adj : adjacent) {
			boolean playeradj = player.contains(adj);
			boolean enemyadj = enemy.contains(adj);

			if (!enemyadj && !playeradj) moveok = true;

			else if (enemyadj && _getlibsbyfield(enemy, player, adj).size()==1) {
				moveok = true;
				removed_stones.addAll(enemy.remgroup(enemy.getgroup(adj)));
			}

		}

		if (!moveok) {
			for (F adj : adjacent) {
				if (player.contains(adj) && _getlibsbyfield(player, enemy, adj).size()>1) {
					moveok = true;
					break;
					//TODO: add tests for correctness of this check
					//TODO: older check threw exception if getadjacent().size()==1, which is wrong
				}
			}
		}

		if (!moveok) return null;

		player.addstone(field);
		return removed_stones;

	}
	//--modifiers--


	// --identity, equality--
	@Override
	public Table<F, G> fork() {
		IColor<F, G> wc = whites.fork();
		IColor<F, G> bc = blacks.fork();
		return new Table<>(fields, adjacency, wc, bc);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Table)) return false;

		Table table = (Table) o;

		if (!blacks.equals(table.blacks)) return false;
		if (!whites.equals(table.whites)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return 31 * whites.hashCode() + blacks.hashCode();
	}
	// --identity, equality--
}
