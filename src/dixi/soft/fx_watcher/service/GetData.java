package dixi.soft.fx_watcher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import dixi.soft.fx_watcher.R;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class GetData extends AsyncTask<Void, Integer, Void> {

	public static abstract interface GetDataListener {
		public abstract void onDownloadDone(Context Context);
	}

	private final int MANAGER_INDEX = 2;
	private final int DEPOSIT_INDEX = 5;
	private final int AVAILABLE_AMOUNT_INDEX = 6;
	private final int PROFIT_INDEX = 7;

	private final String FX_LOGIN_URL = "https://fx-trend.com/login/my/profile/info";
	private final String FX_PAMM_URL = "https://fx-trend.com/my/pamm_investor/accounts/";

	private Context context;
	private GetDataListener getDataListener;

	public GetData(Context context, GetDataListener getDataListener) {

		this.context = context;
		this.getDataListener = getDataListener;
	}

	private String getManagerName(String str) {
		String re1 = ".*?"; // Non-greedy match on filler
		String re2 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re3 = ".*?"; // Non-greedy match on filler
		String re4 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re5 = ".*?"; // Non-greedy match on filler
		String re6 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re7 = ".*?"; // Non-greedy match on filler
		String re8 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re9 = ".*?"; // Non-greedy match on filler
		String re10 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re11 = ".*?"; // Non-greedy match on filler
		String re12 = "(?:[a-z][a-z]+)"; // Uninteresting: word
		String re13 = ".*?"; // Non-greedy match on filler
		String re14 = "((?:[a-z][a-z]+))"; // Word 1

		Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7
				+ re8 + re9 + re10 + re11 + re12 + re13 + re14,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return m.group(1);
		} else
			return str;
	}

	private String getManagerNumber(String str) {
		String re1 = ".*?"; // Non-greedy match on filler
		String re2 = "\\d+"; // Uninteresting: int
		String re3 = ".*?"; // Non-greedy match on filler
		String re4 = "\\d+"; // Uninteresting: int
		String re5 = ".*?"; // Non-greedy match on filler
		String re6 = "(\\d+)"; // Integer Number 1

		Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return m.group(1);
		} else
			return str;
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}

		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicHttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Authenticate on server
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(FX_LOGIN_URL);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("login", PammData
					.getLogin(context)));
			nameValuePairs.add(new BasicNameValuePair("pass", PammData
					.getPass(context)));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			EntityUtils.toString(httpclient.execute(httppost, localContext)
					.getEntity());

			// Get data after authenticate
			HttpGet get = new HttpGet(FX_PAMM_URL);
			HttpResponse response = httpclient.execute(get, localContext);

			String s = EntityUtils.toString(response.getEntity());

			Document doc = Jsoup.parse(s);
			Elements table = doc.select("tr.dt_actual");

			if (table.size() == 0) {
				publishProgress(2);
				publishProgress(0);
				return null;
			} 
			
			PammData.clearPammData();
				
			// Save data
			for (int i = 0; i < table.size(); i++) {
				Elements td = table.get(i).children();
				if (td.size() > 1 && td.size() > 6) {
					PammData.addPammData(getManagerName(td.get(MANAGER_INDEX)
							.html()), getManagerNumber(td.get(MANAGER_INDEX).html()),
							td.get(DEPOSIT_INDEX).html(),
							td.get(AVAILABLE_AMOUNT_INDEX).html(),
							td.get(PROFIT_INDEX).html());
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Error of getting data
			publishProgress(1);
		}
		
		publishProgress(0);
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... value) {
		super.onProgressUpdate(value);
		
		switch (value[0]) {
		case 0:
			getDataListener.onDownloadDone(context);
			break;
		case 1:
			Toast toast1 = Toast.makeText(context, R.string.get_data_error,
					Toast.LENGTH_SHORT);
			toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast1.show();
			break;
		case 2:
			Toast toast2 = Toast.makeText(context, R.string.wrong_login_pass,
					Toast.LENGTH_SHORT);
			toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast2.show();
			break;
		}
			
	}

}
