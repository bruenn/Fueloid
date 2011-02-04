package biz.bruenn.fueloid;

import java.text.DateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.TextView;
import biz.bruenn.fueloid.EditFillUp;
import biz.bruenn.fueloid.data.FillUp;
import biz.bruenn.fueloid.data.FueloidDBProxy;
import biz.bruenn.fueloid.data.Statistic;
import biz.bruenn.fueloid.data.StatisticFillupColumns;

public class FillUpList extends ListActivity {
	FueloidDBProxy mDBProxy;
	FillUpAdapter mFillUpAdapter;
	Statistic mStatistic;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         
        mDBProxy = new FueloidDBProxy(this);
        mStatistic = new Statistic(this);
        
        TextView addButton = (TextView)findViewById(R.id.addFillup);
        addButton.setOnClickListener(mOnClickListener);
        
        
	    mFillUpAdapter = new FillUpAdapter(this, StatisticFillupColumns.getFillUpsForStatistic(mDBProxy.mOpenHelper, mStatistic));
	    

	    this.setListAdapter(mFillUpAdapter);
	    this.getListView().setOnItemClickListener(new FillUpListOnItemClickListener());
	    registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_fillup, menu);
	}
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
    	case R.id.delete:
    		FillUp f = this.mDBProxy.getFillUp(info.id);
    		mStatistic.removeFillUp(f);
    		mFillUpAdapter.changeCursor(mStatistic.getFillUpsCursor());
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    @Override
    public void onResume() {
    	super.onResume();    	
    	//reread fillup list from database, by updating the list adapters cursor
    	((FillUpAdapter)this.getListAdapter()).changeCursor(StatisticFillupColumns.getFillUpsForStatistic(mDBProxy.mOpenHelper, mStatistic));
    	TextView mDistance = (TextView)this.findViewById(R.id.statisticDistance);
    	mDistance.setText(mStatistic.getDistance() + "km|"
    					+ mStatistic.getMoney() + "€|"
    					+ mStatistic.getLiter() + "l|"
    					+ mStatistic.getLiterPerDistance() + "l/km|"
    					+ mStatistic.getMoneyPerLiter() + "€/l");
    }
    
    private class FillUpAdapter extends CursorAdapter {

		public FillUpAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView titel = (TextView) view.findViewById(R.id.title);
			if(null != titel) {
				titel.setText(DateFormat.getDateInstance().format(cursor.getLong(cursor.getColumnIndex(FillUp.FILLDATE)))
							+ " | " + cursor.getInt(cursor.getColumnIndex(FillUp.DISTANCE)) + "km");
			}
			TextView text  = (TextView) view.findViewById(R.id.text);
			if(null != text) {
				text.setText(cursor.getFloat(cursor.getColumnIndex(FillUp.LITER)) + "l; " + cursor.getFloat(cursor.getColumnIndex(FillUp.MONEY)) + "€");
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = LayoutInflater.from(context).inflate(R.layout.fillup_row, parent, false);
			return view;
		}
    	
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	@Override
		public void onClick(View v) {
    	FillUp lastFillUp = mStatistic.getLastFillUp();
    	FillUp newFillUp = null;
    	if(null != lastFillUp) {
    		newFillUp = mDBProxy.insertFillUp(lastFillUp.getmDistance() + 1, new Date(), lastFillUp.getmLiter(), lastFillUp.getmMoney());
    	} else {
    		newFillUp = mDBProxy.insertFillUp(0, new Date(), 0f, 0f);
    	}
		mStatistic.addFillUp(newFillUp);
		Intent i = new Intent(v.getContext(), EditFillUp.class);
		i.putExtra(FillUp.TABLE_NAME, newFillUp.getmId());
		startActivityForResult(i, 0);
    	}
	};
    
    private class FillUpListOnItemClickListener implements OnItemClickListener {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		Cursor c = ((FillUpAdapter)parent.getAdapter()).getCursor();
    		
    		Intent i = new Intent(view.getContext(), EditFillUp.class);
			i.putExtra(FillUp.TABLE_NAME, c.getLong(c.getColumnIndex(FillUp._ID)));
			startActivityForResult(i, 0);			
		}    	
    }
}