package ds.confineme;

import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class systemScan extends ActionBarActivity {
    RelativeLayout layoutSystemScan;
    TextView systemStatus;
    Context context;

    static int riskFactor = 0;
    static String recommend = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_scan);
        layoutSystemScan = (RelativeLayout) findViewById(R.id.relLay);
        systemStatus = (TextView) findViewById(R.id.sysStatus);
        context = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_scan, menu);
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

    /*
    * Get Recommendations on System Scan clicked
    * */
    public void sysGetRecoClicked(View V){
        systemStatus.setText(recommend);
    }

    public void systemScanClicked(View V){

        riskFactor = 0;
        recommend = "";
        if(deviceRooted()){
            riskFactor += 35;
        }
        if(!lockPatternExists()){
            riskFactor += 10;
            recommend += ("Lock pattern should be set\n");
        }
        if(!lockPatternDisplayed()){
            riskFactor += 10;
            recommend += ("Lock pattern should be hidden when entered\n");
        }
        if(nonMarketAppsAllowed()){
            riskFactor += 25;
            recommend += ("Applications from unknown sources should be blocked\n");
        }
        if(!deviceProvisioned()){
            riskFactor += 5;
        }
        if(USBDebuggingEnabled()){
            riskFactor += 10;
            recommend += ("USB Debugging should be turned off unless required\n");
        }
        if(passwordVisibleEnabled()){
            riskFactor += 5;
            recommend += ("Password Visibility should be turned off\n");
        }
        if (riskFactor >= 70){
            systemStatus.setBackgroundColor(Color.parseColor("#FF84FF7E"));
        }
        else if (riskFactor >= 30 && riskFactor < 70){
            systemStatus.setBackgroundColor(Color.parseColor("#FFFFFF93"));
        }
        else {
            systemStatus.setBackgroundColor(Color.parseColor("#FFFF8278"));
        }

        systemStatus.setText(riskFactor+"% secure!");
    }

    /*
    * Check whether device is rooted
    * */

    public boolean deviceRooted(){

        String osBTags = android.os.Build.TAGS;
        if(osBTags != null && osBTags.contains("test-keys")){
            return true;
        }

        Process p = null;
        BufferedReader processIn = null;
        try {
            p = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            processIn = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if (processIn.readLine() != null){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null){
                p.destroy();
            }
        }

        String[] searchdirectories = {"system/app/Superuser.apk", "/system/bin/su", "/system/xbin/su",
                "/system/bin/failsafe/su","/data/local/su","/data/local/xbin/su", "/data/local/bin/su",
                "/system/sd/xbin/su","/sbin/su" };

        int i = 0;
        while(i < searchdirectories.length){
            if (new File(searchdirectories[i]).exists()){
                return true;
            }
            i++;
        }
        return false;
    }

/*
* Checks whether USB Debugging is enabled
* */
    public boolean USBDebuggingEnabled(){

        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1);

    }

    /*
* Checks whether the device is password/PIN protected
* */
    public boolean lockPatternDisplayed(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCK_PATTERN_VISIBLE,2) == 1);
    }

/*
* Checks whether the device is password/PIN protected
* */
    public boolean lockPatternExists(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCK_PATTERN_ENABLED,2) == 1);
    }

    public boolean nonMarketAppsAllowed(){

        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 2) == 1);

    }
/*
* Checks whether device has the Device Administration API enabled
* */
    public boolean deviceProvisioned(){

        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0) == 1);

    }
/*
* Checks whether the setting Make Password Visible is enabled
* */
    public boolean passwordVisibleEnabled(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD, 0) == 1);
    }



}
