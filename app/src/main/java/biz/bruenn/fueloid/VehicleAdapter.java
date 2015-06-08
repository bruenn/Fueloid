package biz.bruenn.fueloid;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import biz.bruenn.fueloid.data.Vehicle;

class VehicleAdapter extends CursorAdapter {

	public VehicleAdapter(Context context, Cursor c) {
		super(context, c, true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = LayoutInflater.from(context).inflate(R.layout.icon_list_item, viewGroup, false);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		icon.setImageDrawable(context.getResources().getDrawable(R.drawable.distance));
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(cursor.getString(cursor.getColumnIndex(Vehicle.TITLE)));
	}
}
