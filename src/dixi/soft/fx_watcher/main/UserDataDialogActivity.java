package dixi.soft.fx_watcher.main;

import dixi.soft.fx_watcher.R;
import dixi.soft.fx_watcher.service.PammData;
import dixi.soft.fx_watcher.widgets.WidgetProvider;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserDataDialogActivity extends Activity {

	private Intent resultIntent;

	private EditText loginEditText;
	private EditText passEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    Intent intent = getIntent();
	    Bundle extras = intent.getExtras();
	    
	    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	    if (extras != null) {
	      widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
	          AppWidgetManager.INVALID_APPWIDGET_ID);
	    }

	    if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
	      finish();
	    }
	
	    resultIntent = new Intent();
	    resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

		setResult(RESULT_CANCELED, resultIntent);

		setContentView(R.layout.activity_user_data);

		loginEditText = (EditText) findViewById(R.id.editText1);
		loginEditText.setText(PammData.getLogin(this));
		passEditText = (EditText) findViewById(R.id.editText3);
		passEditText.setText(PammData.getPass(this));
		((Button) findViewById(R.id.button1))
				.setOnClickListener(okButtonClickListener);
		((Button) findViewById(R.id.button2))
				.setOnClickListener(cancelButtonClickListener);
	}

	private OnClickListener okButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (loginEditText.getText().length() < 3) {
				Toast loginToast = Toast.makeText(UserDataDialogActivity.this,
						R.string.wrong_login_length, Toast.LENGTH_SHORT);
				loginToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				loginToast.show();
				return;
			} else if (passEditText.getText().length() < 3) {
				Toast passToast = Toast.makeText(UserDataDialogActivity.this,
						R.string.wrong_pass_length, Toast.LENGTH_SHORT);
				passToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				passToast.show();
				return;
			}

			PammData.saveLogin(UserDataDialogActivity.this, loginEditText
					.getText().toString());
			PammData.savePass(UserDataDialogActivity.this, passEditText
					.getText().toString());

			// Send broadcast to update widget
			Intent intent = new Intent(UserDataDialogActivity.this,
					WidgetProvider.class);
			intent.setAction(WidgetProvider.ACTION_RESTART);
			sendBroadcast(intent);
			
			setResult(RESULT_OK, resultIntent);
			finish();
		}
	};

	private OnClickListener cancelButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
}
