package dixi.soft.fx_watcher.widgets;

import org.json.JSONException;

import dixi.soft.fx_watcher.R;
import dixi.soft.fx_watcher.service.PammData;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class FxViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	public static final String TAG = "FxViewsFactory";
	
	private Context context = null;

	public FxViewsFactory(Context context, Intent intent) {
		this.context = context;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		return (PammData.getCount());
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews row = new RemoteViews(context.getPackageName(),
				R.layout.list_item);
		try {
			row.setTextViewText(R.id.textView1, PammData.getManager(position)
					+ " - " + PammData.getNumber(position));
			row.setTextViewText(R.id.textView2, PammData.getDeposit(position));
			row.setTextViewText(R.id.textView3, PammData.getAmount(position));
			row.setTextViewText(R.id.textView4, PammData.getProfit(position)
					+ " %");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return (row);
	}
	
	@Override
	public RemoteViews getLoadingView() {
		return (null);
	}

	@Override
	public int getViewTypeCount() {
		return (1);
	}

	@Override
	public long getItemId(int position) {
		return (position);
	}

	@Override
	public boolean hasStableIds() {
		return (true);
	}

	@Override
	public void onDataSetChanged() {
	}
}