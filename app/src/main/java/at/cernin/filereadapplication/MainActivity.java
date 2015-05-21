package at.cernin.filereadapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;

import static at.cernin.filereadapplication.Configuration.checkDebug;


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

    /**
     * Controller der Activity, der die Anzahl und Eigenschaften der Fragen kennt und
     * verwaltet.
     */
    public Controler controler;
    private final String ITEM_IN_BUNDLE = "Preselected Item";
    public int itemSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* XML designed activity-layout */
        controler = new Controler(this, R.raw.controler);

        setContentView(R.layout.activity_main);
        checkDebug(this);
        setLayoutManualy();


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        // Merkt sich die ausgewählte Frage bei einem Orientierungswechsel des Phones
        if (outState != null) {
            outState.putInt(ITEM_IN_BUNDLE, itemSelected);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // Merkt sich die ausgewählte Frage bei einem  Orientierungswechsel des Phones
        if (savedInstanceState != null) {
            itemSelected = savedInstanceState.getInt(ITEM_IN_BUNDLE, 0);
        }


    }

    /**
     * Manageable Controls from all  Methodes
     */
    private SVGImageView myView; // View for Question
    // ToggleButtons for Answers
    private final int MAXBUTTONS = 4;
    private SVGToggleButton myButton[] = new SVGToggleButton[ MAXBUTTONS ];

    /*
        Manually designed activity-layout
        Aufbau des Views abhängig vom Display und der Auflösung des Anzeigegeräts
        Scrollview - Linear-Layout - ImageView - 4 x Tooglebutton
    */

    private void setLayoutManualy() {
        //final int[] myColor = {Color.YELLOW,Color.LTGRAY,Color.CYAN,Color.WHITE};

        // Image View for SVG-Information
        myView = new SVGImageView( this );
        //myView.setBackgroundColor(Color.GRAY);
        //Drawable myDrawable = new ColorDrawable(Color.GREEN);
        //Drawable myDrawable = new SvgDrawable();
        //myView.setBackground(myDrawable);

        for (int i = 0; i < MAXBUTTONS; i++) {
            myButton[i] = new SVGToggleButton( this );
            /*
            myButton[i].setBackgroundColor(myColor[i]);
            myButton[i].setTextOn("Button " + i + " ON");
            myButton[i].setTextOff("Button " + i + " OFF");
            myButton[i].setText(
                    myButton[i].isChecked() ? myButton[i].getTextOn() : myButton[i].getTextOff()
            );
            */
        }

        // configuring the LinearLayout inside the ScrollView
        LinearLayout myLayout = (LinearLayout) findViewById( R.id.container);
        //myLayout.setBackgroundColor(Color.MAGENTA);
        myLayout.setOrientation(LinearLayout.VERTICAL);

        // LayoutParameter before inserting the ImageView and ToggleButtons
        LinearLayout.LayoutParams myParams =
                new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        myParams.gravity = Gravity.CENTER_HORIZONTAL;

        // Calculate from XML-Display-Metric to native Pixel
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int px = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 200, dm );
        int marg = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);

        // Margins between Fields and Buttons
        if (myLayout.getOrientation() == LinearLayout.VERTICAL) {
            // myParams.setMargins(0, marg, 0, marg);
            myParams.setMargins(0, 1, 0, 1);
        }
        else {
            // myParams.setMargins( marg, 0, marg, 0);
            myParams.setMargins( 1, 0, 1, 0);
        }
        //myView.setPadding(marg/2, marg, marg/2, marg);


        // configure Size and insert ImageView into the Layout
        // myView.setMaxHeight(px);
        // myView.setMaxWidth(px);
        myView.setMinimumHeight(px/2);
        myView.setMinimumWidth(px);

        myLayout.addView( myView, myParams);

        // Configure size and insert ToggleButtons into the Layout
        for (ToggleButton tb: myButton) {
            tb.setMinimumWidth(px);
            tb.setMinimumHeight(px/8);

            // Ohne View-ID kein Speichern des Status
            tb.setId(generateViewId( this ));
            myLayout.addView(tb, myParams );
        }

        fillControls( itemSelected );

    }


    private void fillControls(int questionNumber) {
        if ((controler != null) && (controler.questions != null)
                && (controler.questions.size() > questionNumber)
                ) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float ratio = 0, widthRatio = 0;
            float height = 0, sumheight = 0, viewHeight = 0;
            float width = dm.widthPixels;

            if (myView != null) {
                try {
                    myView.setImageViewAsset(this, controler.questions.get(questionNumber).questionFile);
                } catch (SVGParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ratio = myView.svg.getDocumentAspectRatio(); // width/height
                if (ratio > 0) {
                    height = width / ratio;
                }
                else {
                    height = width;
                }
                if (height >= dm.heightPixels) {
                    height = dm.heightPixels-1;
                }
                viewHeight = height;
                myView.setMinimumHeight((int) height);
                widthRatio = myView.svg.getDocumentWidth()/(width-4);
                myView.svg.setDocumentWidth(width - 4);
                myView.svg.setDocumentHeight(height - 4);
            }
            if (controler.questions.get(questionNumber).answerFiles != null &&
                    myButton != null ) {
                for (int i = 0; i < MAXBUTTONS; i++) {
                    if (myButton[i] != null) {
                        if (i < controler.questions.get(questionNumber).answerFiles.size()) {
                            try {
                                myButton[i].setImageViewAsset(this,
                                        controler.questions.get(questionNumber).answerFiles.get(i).answerFile
                                );
                            } catch (SVGParseException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            myButton[i].setChecked(
                                    controler.questions.get(questionNumber).answerFiles.get(i).answerTrue
                            );
                            ratio = myButton[i].svg.getDocumentAspectRatio(); // width/height
                            height = 0;
                            width = dm.widthPixels;
                            float wr = myButton[i].svg.getDocumentWidth()/width;
                            if (wr < widthRatio) {
                                width = myButton[i].svg.getDocumentWidth()/widthRatio;
                            }
                            if (ratio > 0) {
                                height = width / ratio;
                            }
                            else {
                                height = width;
                            }
                            if (height >= dm.heightPixels) {
                                height = dm.heightPixels-1;
                            }
                            myButton[i].setMinimumHeight((int)height);
                            sumheight += height+5;
                            myButton[i].svg.setDocumentWidth(width - 4);
                            myButton[i].svg.setDocumentHeight(height - 4);

                            myButton[i].setVisibility(View.VISIBLE);
                            myButton[i].invalidate();
                        } else {
                            myButton[i].setVisibility(View.GONE);
                        }
                    }
                }
            }
            if (myView != null) {
                if (sumheight+viewHeight < dm.heightPixels-120) {
                    viewHeight = dm.heightPixels-120-sumheight;
                    myView.setMinimumHeight((int)viewHeight);
                }
                myView.invalidate();
            }
        }
        else {
            // Anzeigen, dass es keine gültige ausgewählte Frage gibt!
            // oder Alternativ die 0te Frage anzeigen.
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


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        itemSelected = position;
        fillControls(itemSelected);
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
            // falsch: Intent intent = new Intent(getString(R.string.settings_activity));
            Intent intent = new Intent(this, InformationActivity.class);
            startActivity(intent);
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
