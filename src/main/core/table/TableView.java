package core.table;

import core.color.ColorView;
import core.primitives.Stone;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by maxus on 28.03.16.
 */
public interface TableView<F, G> {

	public Collection<F> getfields();
	public Function<F, Collection<F>> getadjacency();

	public ColorView<F, G> getview(Stone s);
	public Stone getstone(F field);
	public Set<F> getlibs(Stone s, G group);
	public int emptyadjacents(F field);

}
