package net.narlab.projectnar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.narlab.projectnar.R;
import net.narlab.projectnar.general.Nar;
import net.narlab.projectnar.general.NarList;

/**
 * @author fma
 * @date 07.08.2014.
 */
public class NarListAdapter extends ArrayAdapter<Nar> {

	private final Context context;
	private final NarList narList;

	public NarListAdapter(Context context, NarList narList) {

		super(context, R.layout.nar_list_item, narList);

		this.context = context;
		this.narList = narList;
	}

	private static class NarViewHolder {
		public TextView titleView;
	}

	NarViewHolder narViewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// Get rowView from inflater
			convertView = View.inflate(context, R.layout.nar_list_item, null);

			narViewHolder = new NarViewHolder();
			narViewHolder.titleView = (TextView) convertView.findViewById(R.id.nar_item_id);
			convertView.setTag(narViewHolder);
		} else {
			narViewHolder = (NarViewHolder) convertView.getTag();
		}

		narViewHolder.titleView.setText(narList.get(position).getName());
		narViewHolder.titleView.setTextColor(context.getResources().getColor(R.color.black));

		// return view
		return convertView;
	}

	public void add(String narId, String name, String lastalive) {
		add(new Nar(narId, name, lastalive));
	}
	@Override
	public void add(Nar nar) {
		narList.add(nar);
		this.notifyDataSetChanged();
	}

	public void remove(String item_id) {
		narList.remove(item_id);
//		Log.i("NL", narList.toString());
		this.notifyDataSetChanged();
	}

	public Nar get(int position) {
		return narList.get(position);
	}

}
