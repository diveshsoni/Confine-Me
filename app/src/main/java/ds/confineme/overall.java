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


public class overall extends ActionBarActivity {
    RelativeLayout overallSystemScan;
    TextView overallStatus;
    Context context;
    static int systemriskFactor, deviceriskfactor = 0;
    static String systemrecommend, devicerecommend, rec = "";
    static double risk = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);
        overallSystemScan = (RelativeLayout) findViewById(R.id.fullsys);
        context = getApplicationContext();
        overallStatus = (TextView) findViewById(R.id.overallStat);
    }

    public void fullSystScanClicked(View V){
        risk = 0;
        rec = "";

        deviceScanClicked();
        systemScanClicked();

        risk += deviceriskfactor * 0.4;
        risk += systemriskFactor * 0.6;

        rec = systemrecommend + devicerecommend;

        if (risk >= 70){
            overallStatus.setBackgroundColor(Color.parseColor("#FF84FF7E"));
        }
        else if (risk >= 30 && risk < 70){
            overallStatus.setBackgroundColor(Color.parseColor("#FFFFFF93"));
        }
        else {
            overallStatus.setBackgroundColor(Color.parseColor("#FFFF8278"));
        }
        overallStatus.setText(risk+"% secure!");
    }

    public void getoverallRec(View V){
        overallStatus.setText(rec);
    }

    public void deviceScanClicked(){
        deviceriskfactor = 0;
        devicerecommend = "";
        if(!accessibilityEnabled()){
            deviceriskfactor += 35;
        }
        if(!activityDependency()){
            deviceriskfactor += 10;
        }
        if(speakPasswordEnabled()){
            deviceriskfactor += 15;
            devicerecommend += ("Speak Password in Accessibility mode should be disabled unless required\n");
        }
        if(!mockLocationsEnabled()){
            deviceriskfactor += 10;
            devicerecommend  += ("Mock 'Location' Information should be turned on\n");
        }
        if(waitDebugger()){
            deviceriskfactor += 5;
        }
        if(wifiNum()){
            deviceriskfactor += 5;
        }
        if(devSettingsEnabled()){
            deviceriskfactor += 10;
            devicerecommend += ("Developer Settings should be turned off unless required\n");
        }
        if(!systemPropertyVersion()){
            deviceriskfactor += 10;
        }

    }

    /*
    * Check whether Accessibility is enabled
    * */

    public boolean accessibilityEnabled(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0) == 1);
    }

    /*
    * Check if speak password is enabled
    * */
    public boolean speakPasswordEnabled(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD, 0) == 1);
    }

    /*
    * Verify whether the system properties version is up to date
    * */

    public boolean systemPropertyVersion(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.SYS_PROP_SETTING_VERSION, 2) == 1);
    }

    /*
    * Verify whether the developer settings are enabled on the device
    * */
    public boolean devSettingsEnabled(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 2) == 1);
    }
    /*
    * Verify if activity dependencies are allowed in developer options
    * */
    public boolean activityDependency(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 2) == 1);
    }

    /*
    * Verify if activity dependencies are allowed in developer options
    * */
    public boolean waitDebugger(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.WAIT_FOR_DEBUGGER, 2) == 1);
    }

    /*
    * Verify WiFi networks vulnerability
    * */
    public boolean wifiNum(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.WIFI_NUM_OPEN_NETWORKS_KEPT, 2) < 4);
    }

    /*
    * Verify whether Mock Locations are displayed to preserve confidentiality
    * */
    public boolean mockLocationsEnabled(){
        return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION,2) == 1);
    }

    public void systemScanClicked(){

        systemriskFactor = 0;
        systemrecommend = "";
        if(deviceRooted()){
            systemriskFactor += 35;
        }
        if(!lockPatternExists()){
            systemriskFactor += 10;
            systemrecommend += ("Lock pattern should be set\n");
        }
        if(!lockPatternDisplayed()){
            systemriskFactor += 10;
            systemrecommend += ("Lock pattern should be hidden when entered\n");
        }
        if(nonMarketAppsAllowed()){
            systemriskFactor += 25;
            systemrecommend += ("Applications from unknown sources should be blocked\n");
        }
        if(!deviceProvisioned()){
            systemriskFactor += 5;
        }
        if(USBDebuggingEnabled()){
            systemriskFactor += 10;
            systemrecommend += ("USB Debugging should be turned off unless required\n");
        }
        if(passwordVisibleEnabled()){
            systemriskFactor += 5;
            systemrecommend += ("Password Visibility should be turned off\n");
        }
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overall, menu);
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
}
