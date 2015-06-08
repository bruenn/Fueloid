package biz.bruenn.fueloid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import biz.bruenn.fueloid.data.Statistic;

class StatisticAdapter extends ArrayAdapter<Statistic> {

	public StatisticAdapter(Context context, int textViewResourceId, Statistic[] statistics) {
		super(context, textViewResourceId, statistics);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.icon_list_item, parent, false);
		Statistic item = getItem(position);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(item.getTitle());

		TextView text  = (TextView) view.findViewById(R.id.text);
		text.setText(item.getDistance() + "km | " + item.getLiter() + "l | " + item.getMoney() + "€");
		return view;
	}
}
