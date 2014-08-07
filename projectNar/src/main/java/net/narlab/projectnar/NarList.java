package net.narlab.projectnar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fma
 * @date 21.07.2014.
 */
public class NarList extends ArrayList<Nar> {

	private List<Nar> narList;

	public NarList() {
		narList = new ArrayList<Nar>();
	}

	public boolean add(Nar newNar) {
		for (Nar nar: narList) {
			if (nar.getId().equals( newNar.getId() )) {
				return false;
			}
		}
		narList.add(newNar);
		return true;
	}

	@Override
	public Nar get(int position) {
		return narList.get(position);
	}
	public Nar get(Nar nar) {
		return get(nar.getId());
	}
	public Nar get(String id) {
		for (Nar nar: narList) {
			if (nar.getId().equals(id)) {
				return nar;
			}
		}
		return null;
	}

	public boolean remove(Nar nar) {
		return remove(nar.getId());
	}
	public boolean remove(String id) {
		for (Nar nar: narList) {
			if (nar.getId().equals(id)) {
				narList.remove(nar);
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return narList.size();
	}
}
