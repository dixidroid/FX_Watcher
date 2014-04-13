package dixi.soft.fx_watcher.widgets;

import java.util.Arrays;

import dixi.soft.fx_watcher.R;
import dixi.soft.fx_watcher.main.UserDataDialogActivity;
import dixi.soft.fx_watcher.service.GetData;
import dixi.soft.fx_watcher.service.GetData.GetDataListener;
import dixi.soft.fx_watcher.service.PammData;
import dixi.soft.fx_watcher.utils.Config;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider implements GetDataListener {

	public static final String TAG = "WidgetProvider";
	
	public static String ACTION_RESTART = "dixi.soft.fx_watcher.ACTION_RESTART";
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(TAG, "onEnabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.d(TAG, "onDeleted " + Arrays.toString(appWidgetIds));
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d(TAG, "onDisabled");
	}
	  
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		if (Config.DEBUG) {
			Log.i(TAG, "onUpdate");
		}
		
		if (PammData.getLogin(context).equals("")
				|| PammData.getPass(context).equals("")) {
			if (Config.DEBUG) {
				Log.i(TAG, "Выходим - нету логина и пароля!");
			}
			return;
		}
		
		// Loading data
		GetData getData = new GetData(context, this);
		getData.execute();
	}

	@Override
    public void onReceive(Context context, Intent intent) {

		if (Config.DEBUG) {
			Log.i(TAG, "onReceive " + intent.getAction());
		}
		
         //Ловим наш Broadcast, проверяем и выводим сообщение
         final String action = intent.getAction();
         if (action.equals(ACTION_RESTART)) {
        	 updateWidget(context);
         }
         
         super.onReceive(context, intent);
	}
	
	private Intent getAdapterIntent(Context context) {
		Intent svcIntent = new Intent(context, WidgetService.class);
		svcIntent.setData(Uri.parse(svcIntent
				.toUri(Intent.URI_INTENT_SCHEME)));
		return svcIntent;
	}
	
	@Override
	public void onDownloadDone(Context context) {

		if (Config.DEBUG) {
			Log.i(TAG, "onDownloadDone");
		}

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(
				context.getApplicationContext(), WidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		RemoteViews widget = new RemoteViews(context.getPackageName(),
				R.layout.widget);

		// Create action to restart button
		Intent active = new Intent(context, WidgetProvider.class);
		active.setAction(ACTION_RESTART);
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
				context, 0, active, 0);
		// Add action to restart button
		widget.setOnClickPendingIntent(R.id.button1, actionPendingIntent);
		
		// Show update button
		widget.setViewVisibility(R.id.button1, Button.VISIBLE);
		// Show configure button
		widget.setViewVisibility(R.id.button2, Button.VISIBLE);
		// Hide update progress
		widget.setViewVisibility(R.id.progressBar1, Button.GONE);
		
		// Add data to list
		widget.setRemoteAdapter(R.id.listView1, getAdapterIntent(context));
		
		// Update all widgets
		for (int i = 0; i < appWidgetIds.length; i++) {
			// Add action to change user authenticate data
			setClickOnUserChangeAuth(context, widget, appWidgetIds[i]);
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],
			        R.id.listView1);
		}
	}
	
	private void updateWidget(Context context) {	
		
		if (PammData.getLogin(context).equals("")
				|| PammData.getPass(context).equals("")) {
			if (Config.DEBUG) {
				Log.i(TAG, "Выходим - нету логина и пароля!");
			}
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 200; i ++) {
					Log.e(TAG, "" + i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context.getApplicationContext());
		ComponentName thisWidget = new ComponentName(
				context.getApplicationContext(), WidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		RemoteViews widget = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		
		// Hide update button
		widget.setViewVisibility(R.id.button1, Button.GONE);
		// Hide configure button
		widget.setViewVisibility(R.id.button2, Button.GONE);
		// Show update progress
		widget.setViewVisibility(R.id.progressBar1, Button.VISIBLE);		
		
		// Update all widgets
		for (int i = 0; i < appWidgetIds.length; i++) {
			// Add action to change user authenticate data
			setClickOnUserChangeAuth(context, widget, appWidgetIds[i]);
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		if (appWidgetIds != null && appWidgetIds.length > 0) {
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
	}

	private void setClickOnUserChangeAuth(Context context, RemoteViews widget, int appWidgetId) {
		Intent intent = new Intent(context, UserDataDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		widget.setOnClickPendingIntent(R.id.button2, pIntent);
	}
	
}
