package dixi.soft.fx_watcher.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dixi.soft.fx_watcher.utils.Config;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class PammData {
	
	private static final String TAG = "PammData";
	
	private static final String MANAGER_ID = "manager";
	private static final String NUMBER_ID = "number";
	private static final String DEPOSIT_ID = "deposit";
	private static final String AVAILABLE_AMOUNT_ID = "amount";
	private static final String PROFIT_ID = "profit";
	
	private static JSONArray pammData = new JSONArray();
	
	private static String pammLogin;
	private static String pammPass;
	
	/**
	 * Add one PAMM managing account
	 * 
	 * @param manager
	 * @param deposit
	 * @param amount
	 * @param profit
	 * @throws JSONException
	 */
	public static void addPammData(String manager, String number,
			String deposit, String amount, String profit) throws JSONException {
		
		if (Config.DEBUG) {
			Log.i(TAG, manager + " " + deposit + " " + amount + " " + profit + " ");
		}
		
		JSONObject pammObj = new JSONObject();
		pammObj.put(MANAGER_ID, manager);
		pammObj.put(NUMBER_ID, number);
		pammObj.put(DEPOSIT_ID, deposit);
		pammObj.put(AVAILABLE_AMOUNT_ID, amount);
		pammObj.put(PROFIT_ID, profit);
		
		pammData.put(pammObj);
	}
	
	/**
	 * Clear all PAMM data
	 */
	public static void clearPammData() {
		pammData = new JSONArray();
	}
	
	/*
	 * Save/Read data to/from preferences
	 */
	public static void saveLogin(Context context, String login) {
		pammLogin = login;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString("login", login);
		editor.commit();
	}

	public static void savePass(Context context, String pass) {
		pammPass = pass;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString("pass", pass);
		editor.commit();
	}

	public static String getLogin(Context context) {
		if (pammLogin == null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context.getApplicationContext());
			pammLogin = prefs.getString("login", "");
		}

		return pammLogin;
	}

	public static String getPass(Context context) {
		if (pammPass == null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context.getApplicationContext());
			pammPass = prefs.getString("pass", "");
		}

		return pammPass;
	}
	
	/**
	 * Count of PAMM`s
	 */
	public static int getCount() {
		return pammData.length();
	}
	
	/*
	 * Get PAMM data
	 */
	public static String getManager(int index) throws JSONException {
		return pammData.getJSONObject(index).getString(MANAGER_ID);
	}
	
	public static String getNumber(int index) throws JSONException {
		return pammData.getJSONObject(index).getString(NUMBER_ID);
	}
	
	public static String getDeposit(int index) throws JSONException {
		return pammData.getJSONObject(index).getString(DEPOSIT_ID);
	}
	
	public static String getAmount(int index) throws JSONException {
		return pammData.getJSONObject(index).getString(AVAILABLE_AMOUNT_ID);
	}
	
	public static String getProfit(int index) throws JSONException {
		return pammData.getJSONObject(index).getString(PROFIT_ID);
	}
}
