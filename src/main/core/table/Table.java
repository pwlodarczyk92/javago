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
	public Collection<F> getFields() {
		return fields;
	}
	@Override
	public Collection<F> getAdjacency(F node) {
		return adjacency.apply(node);
	}

	@Override
	public ColorView<F, G> getView(Stone s) {
		switch (s) {
			case WHITE: return whites;
			case BLACK: return blacks;
			default: throw new RuntimeException();
		}
	}
	@Override
	public Stone getStone(F field) {
		if (whites.contains(field)) return Stone.WHITE;
		if (blacks.contains(field)) return Stone.BLACK;
		return Stone.EMPTY;
	}
	@Override
	public Set<F> getLiberties(Stone s, G group) {
		switch (s) {
			case WHITE: return _getLibsByGroup(whites, blacks, group);
			case BLACK: return _getLibsByGroup(blacks, whites, group);
			default: throw new RuntimeException();
		}
	}
	//--accessors--


	private Set<F> _getLibsByGroup(IColor<F, G> main, IColor<F, G> substract, G group) {

		HashSet<F> result = new HashSet<>(4);

		for (F lib: main.getAdjacents(group)) {
			if (!substract.contains(lib))
				result.add(lib);
		}

		return result;

	}

	private Set<F> _getLibsByField(IColor<F, G> main, IColor<F, G> substract, F field) {

		return _getLibsByGroup(main, substract, main.getGroup(field));

	}


	//--modifiers--
	@Override
	public Set<F> put(Stone stone, F field) {

		HashSet<F> removedStones = new HashSet<>();
		IColor<F, G> player = stone == Stone.BLACK ? blacks : whites;
		IColor<F, G> enemy = stone == Stone.BLACK ? whites : blacks;

		if (player.contains(field) || enemy.contains(field))
			return null;

		Collection<F> adjacents = adjacency.apply(field);
		boolean isMoveOk = false;

		for (F adjacent : adjacents) {
			boolean playerAdjacent = player.contains(adjacent);
			boolean enemyAdjacent = enemy.contains(adjacent);

			if (!enemyAdjacent && !playerAdjacent) isMoveOk = true;

			else if (enemyAdjacent && _getLibsByField(enemy, player, adjacent).size()==1) {
				isMoveOk = true;
				removedStones.addAll(enemy.removeGroup(enemy.getGroup(adjacent)));
			}

		}

		if (!isMoveOk) {
			for (F adj : adjacents) {
				if (player.contains(adj) && _getLibsByField(player, enemy, adj).size()>1) {
					isMoveOk = true;
					break;
					//TODO: add tests for correctness of this check
					//TODO: older check threw exception if getadjacent().size()==1, which is wrong
				}
			}
		}

		if (!isMoveOk) return null;

		player.addStone(field);
		return removedStones;

	}
	//--modifiers--


	// --identity, equality--
	@Override
	public Table<F, G> fork() {
		return new Table<>(fields, adjacency, whites.fork(), blacks.fork());
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
