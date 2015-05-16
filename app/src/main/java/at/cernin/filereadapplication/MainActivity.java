package at.cernin.filereadapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* XML designed activity-layout */
        setContentView(R.layout.activity_main);
        setLayoutManualy();


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /*
        Manually designed activity-layout
        Aufbau des Views abhängig vom Display und der Auflösung des Anzeigegeräts
        Scrollview - Linear-Layout - ImageView - 4 x Tooglebutton
    */

    private void setLayoutManualy() {
        final int MAXBUTTONS = 4;
        final int[] myColor = {Color.YELLOW,Color.LTGRAY,Color.CYAN,Color.WHITE};

        // Image View for SVG-Information
        ImageView myView = new ImageView( this );
        myView.setBackgroundColor(Color.GRAY);
        //Drawable myDrawable = new ColorDrawable(Color.GREEN);
        Drawable myDrawable = new SvgDrawable();
        //myView.setBackground(myDrawable);

        // ToggleButtons for Answers
        ToggleButton myButton[] = new ToggleButton[ MAXBUTTONS ];

        for (int i = 0; i < MAXBUTTONS; i++) {
            myButton[i] = new ToggleButton( this );
            myButton[i].setBackgroundColor(myColor[i]);
            myButton[i].setTextOn("Button " + i + " ON");
            myButton[i].setTextOff("Button " + i + " OFF");
            myButton[i].setText(
                    myButton[i].isChecked() ? myButton[i].getTextOn() : myButton[i].getTextOff()
            );
            //myButton[i].setBackground(myDrawable);
        }

        // configuring the LinearLayout inside the ScrollView
        LinearLayout myLayout = (LinearLayout) findViewById( R.id.container);
        myLayout.setBackgroundColor(Color.MAGENTA);
        myLayout.setOrientation(LinearLayout.VERTICAL);

        // LayoutParameter before inserting the ImageView and ToggleButtons
        LinearLayout.LayoutParams myParams =
                new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        myParams.gravity = Gravity.CENTER_HORIZONTAL;

        // Calculate from XML-Display-Metric to native Pixel
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int px = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 300, dm );
        int marg = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);

        // Margins between Fields and Buttons
        if (myLayout.getOrientation() == LinearLayout.VERTICAL) {
            myParams.setMargins( 0, marg, 0, marg);
        }
        else {
            myParams.setMargins( marg, 0, marg, 0);
        }


        // configure Size and insert ImageView into the Layout
        // myView.setMaxHeight(px);
        // myView.setMaxWidth(px);
        myView.setMinimumHeight(px);
        myView.setMinimumWidth(px);
        myLayout.addView( myView, myParams);

        // Configure size and insert ToggleButtons into the Layout
        for (ToggleButton tb: myButton) {
            tb.setWidth(px);
            tb.setHeight(px);
            // Ohne View-ID kein Speichern des Status
            //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tb.setId(generateViewId( this ));
            //} else {
            //    tb.setId(View.generateViewId());
            //}
            myLayout.addView(tb, myParams );
        }

    }

     /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */

    private int sNextGeneratedId = 1;

    public int generateViewId( Activity a) {
        for (;;) {
            int result = sNextGeneratedId;
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            sNextGeneratedId = newValue;
            if (a.findViewById(result) == null) {
                return result;
            }
        }
    }

    private class SvgDrawable extends Drawable {

        private Paint paint;
        private Paint whitePaint;

        public SvgDrawable() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            // Foreground-Color: paint.setColor(Color.WHITE);
            whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
        }

        @Override
        public void draw( Canvas canvas) {
            if (isVisible()) {
                int height = getBounds().height();
                int width = getBounds().width();
                canvas.drawPaint(whitePaint);
                canvas.drawLine(0, 0, width, height, paint);
                canvas.drawLine(width, 0, 0, height, paint);
                /*
                if (isStateful()) {
                    int state[] = getState();
                    if (state != null) {
                        for (int st:state) {
                            if (true) {
                                canvas.drawLine(0, height/2, width, height/2, paint);
                                canvas.drawLine(width/2, 0, width/2, height, paint);
                            }
                        }
                    }
                }
                */
            }
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha( alpha );
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter( cf );
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}