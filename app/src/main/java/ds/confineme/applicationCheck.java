package ds.confineme;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

import java.util.ArrayList;


public class applicationCheck extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_check);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_application_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void applicationScan(View V){
        asyncExecution as = new asyncExecution();
        as.execute();
    }

    public class asyncExecution extends AsyncTask<Void, Void, String>{
        TextView t1 = (TextView) findViewById(R.id.textView2);
        ArrayList<String> apps = new ArrayList<>();
        String appName = "";
        MarketSession session = new MarketSession();
        AppsRequest appsRequest = null;

        @Override
        protected String doInBackground(Void... params) {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String udid = telephonyManager.getDeviceId();
            session.getContext().setAndroidId(udid);
            session.login("confineme88@gmail.com", "confineme");

            String query = "maps";
            appsRequest = AppsRequest.newBuilder()
                    .setQuery(query)
                    .setStartIndex(0).setEntriesCount(10)
                    .setWithExtendedInfo(true)
                    .build();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            session.append(appsRequest, new Callback<AppsResponse>() {
                @Override
                public void onResult(ResponseContext context, AppsResponse response) {
                    t1.setText("Hi!");
//                    t1.setText((response.getApp(0).getTitle().equals(""))?"No App Found!":response.getApp(0).getTitle());
                }
            });

            super.onPostExecute(s);
        }
    }
}
