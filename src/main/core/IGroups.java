package core;

import java.util.function.Function;

/**
 * Created by maxus on 20.02.16.
 */
public interface IGroups<F> {
	public boolean contains(F node);
	public F root(F node);
	public void remove(F root);
	public F add(F node);
	public int size();
}
