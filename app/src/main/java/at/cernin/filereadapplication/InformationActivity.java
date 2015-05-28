package at.cernin.filereadapplication;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class InformationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_information, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getString(R.string.package_name),
                    PackageManager.GET_CONFIGURATIONS
            );
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pi != null) {
            TextView tv = (TextView) findViewById(R.id.txtVersion);
            tv.setText(pi.versionName);
            tv = (TextView) findViewById(R.id.txtVersionIntern);
            tv.setText(Integer.toString(pi.versionCode));
            tv = (TextView) findViewById(R.id.txtProduktionsdatum);
            SimpleDateFormat sdf = new SimpleDateFormat("dd'.'MM'.'yyyy");
            tv.setText(sdf.format(AppConfiguration.ProductionDate.getTime()));
        }
        CheckBox chk = (CheckBox) findViewById(R.id.chkDebug);
        chk.setChecked( AppConfiguration.DEBUG );
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
