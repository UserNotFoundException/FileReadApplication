package at.cernin.filereadapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;

import static at.cernin.filereadapplication.AppConfiguration.checkDebug;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        SVGImageListener,
        SVGButtonListener {

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
    public XmlQuestionControler questionControler;

    // Menüauswahl im navigational Drawer und Name für die Speicherung im Bundle
    private final String ITEM_IN_BUNDLE = "Preselected Item";
    public int itemSelected = 0;

    // Zugang zu den Displayeigenschaften schaffen und grundsätzliche Berechnungen durchführen
    private Display display;


    // View-Fameworkfunktionen nur aufrufen, wenn die OnCreate fertig ist
    private boolean creationFinished = false;


    // Festhalten ob eine Ansicht mit einem oder mit 2 Views vorliegt
    int viewCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Merkt sich die ausgewählte Frage bei einem  Orientierungswechsel des Phones
        if (savedInstanceState != null) {
            itemSelected = savedInstanceState.getInt(ITEM_IN_BUNDLE, 0);
        }

        // Zugang zu den Displayeigenschaften schaffen und
        // grundsätzliche Berechnungen durchführen
        display = new Display(
                    getResources().getDisplayMetrics(),
                    getResources().getConfiguration()
                );

        // Läd das XML-Steuerungsfile aus dem res/raw-Verzeichnis
        questionControler = new XmlQuestionControler(this, R.raw.question_and_answers);

        // Prüft ob das Debugging eingeschaltet ist
        checkDebug(this);

        // Layout laden
        setContentView(R.layout.activity_main);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /*
        LinearLayout basisLayout = (LinearLayout) findViewById( R.id.basislayout);
        TextView tv = new TextView(this);
        tv.setText("Basislayout - Das ist ein Test");
        basisLayout.addView(tv);
        // Linear Layout
        LinearLayout linearLayout = new LinearLayout( this );
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(50);
        linearLayout.setMinimumWidth(50);
        linearLayout.setVisibility(LinearLayout.VISIBLE);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ); // width, height, weight
        linearLayout.setBaselineAligned( false );
        linearParams.gravity = Gravity.CENTER_HORIZONTAL;
        linearParams.weight = 1f;
        TextView tv1 = new TextView(this);
        tv1.setText("LinearLayout - Das ist ein Test");
        linearLayout.addView(tv1);
        basisLayout.addView(linearLayout, linearParams);

        LinearLayout.LayoutParams linearParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ); // width, height, weight
        linearLayout.setBaselineAligned( false );
        linearParams.gravity = Gravity.CENTER_HORIZONTAL;
        linearParams1.weight = 1f;
        LinearLayout linearLayout1 = new LinearLayout( this );
        linearLayout1.setMinimumHeight(50);
        linearLayout1.setMinimumWidth(50);
        TextView tv2 = new TextView(this);
        tv2.setText("2 Das ist ein Test 2");
        linearLayout1.addView(tv2);
        basisLayout.addView(linearLayout1, linearParams1);

        LinearLayout linearLayout2 = new LinearLayout( this );
        linearLayout2.setMinimumHeight(50);
        linearLayout2.setMinimumWidth(50);
        basisLayout.addView( linearLayout2, linearParams);
        TextView tv3 = new TextView(this);
        tv3.setText("3 Das ist ein Test 3");
        linearLayout2.addView(tv3);
        */
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        // Ergänzt das Layout aus der activity_main_p_p.xml um dynamische
        // Komponenten und fügt diese in das Basislayout der Activity ein
        setLayoutManualy();

        // Erzeugt den Android-Standard-Navigational Drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        creationFinished = true;
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
     * Dynamicaly generatete controls for the activity
     */
    // Der Fragen-View
    private QuestionView questionView; // View for Question

    // ToggleButtons for Answers
    private final int MAXBUTTONS = 5;
    private final AnswerButton answerButton[] = new AnswerButton[ MAXBUTTONS ];

    LinearLayout contentLayout = null;
    LinearLayout questionLayout = null;
    LinearLayout answersLayout = null;


    /*
        Manually designed activity-layout
        Aufbau des Views abhängig vom Display und der Auflösung des Anzeigegeräts
        Scrollview - Linear-Layout - ImageView - 4 x Tooglebutton
    */

    private void setLayoutManualy() {

        // Farben für die verschiedenen Knöpfe definieren
        //final int[] myColor = {Color.YELLOW,Color.LTGRAY,Color.CYAN,Color.WHITE};

        // Layoutklasse für die Anzeige-Viewes
        LinearLayout basisLayout = (LinearLayout) findViewById( R.id.basislayout);
        // Die bisher aktiven Views löschen
        basisLayout.removeAllViews();
        contentLayout = null;
        questionLayout = null;
        answersLayout = null;


        // Läd die passenden Views aus der Ressourcen-xml-Datei
        switch (questionControler.questions.get(itemSelected).questionTyp) {
            case 1:
                viewCount = 2;
                if (basisLayout != null) {
                    basisLayout.setOrientation(
                            display.landscapeOrientation() ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL
                    );
                    questionLayout = new ScrollLayout(basisLayout, display, this).linearLayout;
                    answersLayout = new ScrollLayout(basisLayout, display, this).linearLayout;
                }

                break;
            case 2:
                viewCount = 1;
                if (basisLayout != null) {
                    basisLayout.setOrientation(
                            display.landscapeOrientation() ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL
                    );
                    contentLayout = new ScrollLayout(basisLayout, display, this).linearLayout;
                }
                break;
            default:
                viewCount = 1;
                final String s1 = getString(R.string.package_name) + " :\n" +
                        this.getComponentName() +  " :\n" +
                        this.getLocalClassName();
                final String s2 = "Ein unbekannter Fragentyp (questionTyp=<" +
                        questionControler.questions.get(itemSelected).questionTyp +
                        ">) kann nicht dargestellt werden!";
                Log.w(s1, s2);
                basisLayout.setOrientation(
                        LinearLayout.VERTICAL
                );
                new ErrorLayout(basisLayout, display, "Fragentypfehler", s1, s2, this);
                //setContentView(R.layout.activity_main);
                //questionLayout = (LinearLayout) findViewById( R.id.container);
                break;
        }

        if (questionLayout != null) {
            questionView = new QuestionView(questionLayout, display, this);
        }

        else if (contentLayout != null) {
            questionView = new QuestionView(contentLayout, display, this);
        }

        if (answersLayout != null) {
            for (int i = 0; i < MAXBUTTONS; i++) {
                answerButton[i] = new AnswerButton(answersLayout, display, this);
            }
        }

        // Den Inhalt für die Controls bereitstellen
        fillControls( itemSelected );

    }


    public class Display {

        final private DisplayMetrics dm;
        final private Configuration cf;

        public Display(DisplayMetrics dm, Configuration cf) {
            this.dm = dm;
            this.cf = cf;
        }

        public int getDDP( float dIP) {
            return (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, dIP, dm
                            );
        }

        public int limitHeight( int height ) {
            if (height >= dm.heightPixels) {
                return dm.heightPixels;
            };
            return height;
        }

        public float getDPI() {
            if (landscapeOrientation()) {
                return dm.ydpi;
            }
            else {
                return dm.xdpi;
            }
        }

        public float getWidth() {
            return dm.widthPixels;
        }

        public float getHeight() {
            return dm.heightPixels;
        }

        public int getQuestionWidth() {
            if (landscapeOrientation()) {
                if ((answersLayout != null) &&
                        ((ScrollView)answersLayout.getParent()).getVisibility() == ScrollView.VISIBLE)
                {
                    return (int) ((dm.widthPixels) / viewCount);
                }
                else {
                    return (int) ((dm.widthPixels));
                }
            }
            else {
                return (int) dm.widthPixels;
            }
        }

        public int getQuestionHeight() {
            if (landscapeOrientation()) {
                return (int) dm.heightPixels;
            }
            else {
                if ((answersLayout != null) &&
                        ((ScrollView)answersLayout.getParent()).getVisibility() == ScrollView.VISIBLE)
                {
                    return (int) (dm.heightPixels) / viewCount;
                }
                else {
                    return (int) dm.heightPixels;
                }
            }
        }

        public boolean landscapeOrientation() {
            return cf.orientation == Configuration.ORIENTATION_LANDSCAPE;
        }

    }

    private class ScrollLayout {
        /**
         * Create a vertical Scrollview and include a Linear Layout
         */
        // Parameter für das äußere Layout
        private final LinearLayout.LayoutParams myLayoutParams;
        // Parameter für den Scrollview
        private final ScrollView scrollView;

        // Das Innerste Layout
        public final LinearLayout linearLayout;
        private final ScrollView.LayoutParams scrollViewParams;

        public ScrollLayout(LinearLayout myLayout, Display display, Activity activity) {

            // Vertical Scroll View
            scrollView = new ScrollView( activity );
            if (display.landscapeOrientation()) {
                myLayoutParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT
                ); // width, height, weight
            }
            else {
                myLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0
                ); // width, height, weight
            }
            myLayoutParams.weight = 1.0f;

            // Linear Layout
            linearLayout = new LinearLayout( activity );

            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setScrollContainer(true);
            // linearLayout.setBaselineAligned( false );
            scrollViewParams = new ScrollView.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
            ); // width, height

            // Das Linear Layout in den ScrollView einfügen
            scrollView.addView( linearLayout, scrollViewParams);
            // Den ScrolLView ins bestehende Layout einfügen
            myLayout.addView( scrollView, myLayoutParams);
        }
    }


    private class ErrorLayout {
        /**
         * Create an Error-Screen an include som Text-information Fields
         */

        public ErrorLayout(
                LinearLayout myLayout, Display display,
                String tt, String t1, String t2, Activity activity
        ) {
            int padding = display.getDDP(5);
            myLayout.setPadding( padding, padding, padding, padding);

            // Überschrift
            TextView tv = new TextView( activity );
            tv.setText(tt);
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextAppearance(activity, android.R.style.TextAppearance_Large);
            myLayout.addView(tv);

            // Erstes Meldungsfeld
            tv = new TextView( activity);
            tv.setText(t1);
            tv.setPadding(2, padding, 2, padding);
            tv.setTextAppearance(activity, android.R.style.TextAppearance_Small);
            myLayout.addView(tv);

            // Zweites Meldungsfeld
            tv = new TextView( activity );
            tv.setText(t2);
            tv.setPadding(2, padding, 2, padding );
            tv.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
            myLayout.addView(tv);
        }
    }

    private class QuestionView {

        private final LinearLayout.LayoutParams myParams;
        public final SVGImageView svgImageView;

        public QuestionView(LinearLayout myLayout, Display display, Activity activity) {
            // Calculate from XML-Display-Metric to native Pixel
            // int px = display.getDDP(200);
            int marg = display.getDDP(3);

            // Image View for SVG-Information
            svgImageView = new SVGImageView(activity);
            //svgImageView.setScrollContainer(true);

            //questionView.setBackgroundColor(Color.GRAY);
            //Drawable myDrawable = new ColorDrawable(Color.GREEN);
            //Drawable myDrawable = new SvgDrawable();
            //questionView.setBackground(myDrawable);

            // configure Size and insert ImageView into the Layout
            // questionView.setMaxWidth(px);
            // questionView.setMaxHeight(px);
            // svgImageView.setMinimumWidth(px / 2);
            // svgImageView.setMinimumHeight(px);
            svgImageView.setMinimumWidth(display.getQuestionWidth());
            svgImageView.setMinimumHeight(display.getQuestionHeight());
            //svgImageView.setScrollContainer(true);

            // OneClickListener#
            svgImageView.setClickable(true);
            svgImageView.setOnClickListener( new View.OnClickListener() {
                 @Override
                 public void onClick( View v ) {
                     if (answersLayout != null) {
                        if (((ScrollView)(answersLayout.getParent())).getVisibility() == LinearLayout.VISIBLE) {
                            ((ScrollView)(answersLayout.getParent())).setVisibility(LinearLayout.GONE);
                        }
                         else {
                            ((ScrollView)(answersLayout.getParent())).setVisibility(LinearLayout.VISIBLE);
                        }
                     }
                 }
                                         }
            );


            // Die Parameter für zum Einfügen in das Layout feslegen
            // LayoutParameter before inserting the ImageView and ToggleButtons
            myParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            additionalLayoutParameter(myLayout);

            // Den neuen View ins bestehende Layout einfügen
            myLayout.addView( svgImageView, myParams);

        }

        private void additionalLayoutParameter(LinearLayout myLayout) {

            myParams.gravity = Gravity.CENTER_HORIZONTAL;

            // Margins between Fields and Buttons
            if (myLayout.getOrientation() == LinearLayout.VERTICAL) {
                // myParams.setMargins(0, marg, 0, marg);
                myParams.setMargins(0, 1, 0, 1);
            }
            else {
                // myParams.setMargins( marg, 0, marg, 0);
                myParams.setMargins( 1, 0, 1, 0);
            }
            //questionView.setPadding(marg/2, marg, marg/2, marg);

        }

    }

    private class AnswerButton {

        private final LinearLayout.LayoutParams myParams;
        private final SVGToggleButton svgToggleButton;

        public AnswerButton(LinearLayout myLayout, Display display, Activity activity) {
            // Calculate from XML-Display-Metric to native Pixel
            //int px = display.getDDP(200);
            //int marg = display.getDDP(3);

            // ToggleButton with SVG-Information
            svgToggleButton = new SVGToggleButton( activity );

            /*
            myButton[i].setBackgroundColor(myColor[i]);
            myButton[i].setTextOn("Button " + i + " ON");
            myButton[i].setTextOff("Button " + i + " OFF");
            myButton[i].setText(
                    myButton[i].isChecked() ? myButton[i].getTextOn() : myButton[i].getTextOff()
            );
            */

            // configure Size and insert SVDToggleButton into the Layout
            // svgToggleButton.setMaxHeight(px);
            // svgToggleButton.setMaxWidth(px);
            svgToggleButton.setMinimumHeight(display.getQuestionHeight( )/MAXBUTTONS);
            svgToggleButton.setMinimumWidth(display.getQuestionWidth( ));
            // Ohne View-ID kein automatisches Speichern des Status beim abbrechen der
            // Activity
            svgToggleButton.setId(generateViewId(activity));

            // Die Parameter für zum Einfügen in das Layout festlegen
            // LayoutParameter before inserting the ImageView and ToggleButtons
            myParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            additionalLayoutParameter(myLayout);

            // Den neuen Button ins bestehende Layout einfügen
            myLayout.addView( svgToggleButton, myParams);

        }

        private void additionalLayoutParameter(LinearLayout myLayout) {

            myParams.gravity = Gravity.CENTER_HORIZONTAL;

            // Margins between Fields and Buttons
            if (myLayout.getOrientation() == LinearLayout.VERTICAL) {
                // myParams.setMargins(0, marg, 0, marg);
                myParams.setMargins(0, 1, 0, 1);
            }
            else {
                // myParams.setMargins( marg, 0, marg, 0);
                myParams.setMargins( 1, 0, 1, 0);
            }
            //questionView.setPadding(marg/2, marg, marg/2, marg);

        }

    }

    private void fillControls(int questionNumber) {
        if ((questionControler != null) && (questionControler.questions != null)
                && (questionControler.questions.size() > questionNumber)
                ) {

            // Das Fragenfenster managen
            if (questionView != null) {
                questionView.svgImageView.setSVGImageListener(this);
                questionView.svgImageView.setImageViewAsset(this, questionControler.questions.get(questionNumber).questionFile);
            }

            if (questionControler.questions.get(questionNumber).answerFiles != null && answerButton != null ) {
                boolean singleSelection = false, multipleSelection = false;
                for (int j = 0;
                     j < questionControler.questions.get(questionNumber).answerFiles.size();
                     j++)
                { // Festlegen ob es eine oder mehrere korrekte Antworten gibt
                    if (questionControler.questions.get(questionNumber).answerFiles.get(j).answerTrue) {
                        if (singleSelection) {
                            multipleSelection = true;
                            break;
                        } else {
                            singleSelection = true;
                        }
                    }
                }
                for (int i = 0; i < MAXBUTTONS; i++) {
                    if (answerButton[i] != null) {
                        if (i < questionControler.questions.get(questionNumber).answerFiles.size()) {
                            answerButton[i].svgToggleButton.setSVGButtonListener(this);
                            answerButton[i].svgToggleButton.setImageViewAsset(this,
                                    questionControler.questions.get(questionNumber).answerFiles.get(i).answerFile
                            );
                            answerButton[i].svgToggleButton.setChecked(
                                    questionControler.questions.get(questionNumber).answerFiles.get(i).answerTrue
                            );
                            // Defaultmäßig hat ein Button ein Drawable als Hintergrund hinterlegt
                            // das vor dem onDraw gezeichnet wird. Dieses stört bei SVG-Buttons.
                            answerButton[i].svgToggleButton.setBackground(null);
                            // Anzeigen von Kreisen oder Vierecken für einfach und Mehrfachauswahl in den Buttons
                            answerButton[i].svgToggleButton.multipleSelection = multipleSelection;
                        } else {
                            answerButton[i].svgToggleButton.setVisibility(View.GONE);
                        }
                    }
                }
            }
            /*
            if (questionView != null) {
                if (sumheight+viewHeight < display.dm.heightPixels*7/10) {
                    viewHeight = display.dm.heightPixels*7/10-sumheight;
                    questionView.svgImageView.setMinimumHeight((int)viewHeight);
                }
                questionView.svgImageView.invalidate();
            }
            */
        }
        else {
            // Anzeigen, dass es keine gültige ausgewählte Frage gibt!
            // oder Alternativ die 0te Frage anzeigen.
        }

    }

    /**
     * Is Called, when the questionVie (SVGImageViewer) has loaded the
     * SVG-File from the asset or from the ressource
     * @param svgImageView
     */
    @Override
    public void SVGImageLoaded(SVGImageView svgImageView) {

        float svgDPI;
        float ratio;
        float height;
        float width;

        if (null != svgImageView.svg) {

            // Bestimme die View-Größe
            svgDPI = svgImageView.documentRenderDPI;
            if (svgDPI == 0) {
                svgDPI = 96;
            }
            ratio = display.getDPI() / svgDPI;
            // Doppelte größe, wenn nur ein Fragen-Display angezeigt wird
            if (display.landscapeOrientation()) {
                if (contentLayout != null || (
                    (answersLayout != null) &&
                    (((ScrollView)answersLayout.getParent()).getVisibility() != ScrollView.VISIBLE))
                ) {
                    ratio *= 2;
                }
            }
            width = svgImageView.documentWidth * ratio;
            if (width > display.getQuestionWidth()) {
                ratio *= display.getQuestionWidth() / width;
            }
            width = svgImageView.documentWidth * ratio;
            height = svgImageView.documentHeight * ratio;
                /*
                ratio = questionView.svgImageView.svg.getDocumentAspectRatio(); // width/height
                // Lege den View im selben Verhältnis fest, wie die darzustellende Grafik
                if (ratio > 0) {
                    height = width / ratio;
                }
                else { // Ratio = 0 bedeutet, sie läßt sich nicht berechnen
                    height = width;
                }
                */
            // Die Grafik darf nicht höher als die Bildschirmhöhe werden
            height = display.limitHeight( (int) height ); // weg

            // Die Größe der Fragengrafik an den View anpassen.
            svgImageView.svg.setDocumentWidth(width);
            svgImageView.svg.setDocumentHeight(height);

            /*
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) svgImageView.getLayoutParams();
            //params assumed to be <> null, because i insert the view with LayoutParams while
            if (height < display.getQuestionHeight()*0.8f) {
                params.height = (int) (display.getQuestionHeight()*0.8f);
            }
            else {
                params.height = (int) (height+0.1f*display.getQuestionHeight());
            }
            svgImageView.setLayoutParams(params);
            */
            /**/
            if (height > svgImageView.getMinimumHeight()) {
                svgImageView.setMinimumHeight((int)height);
            }
            /**/
        }

        //svgImageView.requestLayout();
        svgImageView.invalidate();
    }


    @Override
    public void SVGImageLoaded(SVGToggleButton svgToggleButton) {
        float svgDPI;
        float ratio;
        float height;
        float width;

        // Bestimme die View-Größe
        svgDPI = svgToggleButton.svg.getRenderDPI();
        if (svgDPI == 0) {
            svgDPI = 96;
        }
        ratio = display.getDPI()/svgDPI;
        width = svgToggleButton.svg.getDocumentWidth() * ratio;
        if (width > display.getQuestionWidth()*.83f) {
            ratio *= display.getQuestionWidth()*.83f/width;
        }

        width = svgToggleButton.svg.getDocumentWidth() * ratio;
        height = svgToggleButton.svg.getDocumentHeight() * ratio;

        height = display.limitHeight((int) height ); // weg

        svgToggleButton.svg.setDocumentWidth(width);
        svgToggleButton.svg.setDocumentHeight(height);

        if (height > svgToggleButton.getMinimumHeight()) {
            svgToggleButton.setMinimumHeight((int)height);
        }

        svgToggleButton.setVisibility(View.VISIBLE);
        //svgToggleButton.requestLayout();
        svgToggleButton.invalidate();
    }


    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */

    private int sNextGeneratedId = 1;

    public int generateViewId( Activity activity) {
        for (;;) {
            int result = sNextGeneratedId;
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            sNextGeneratedId = newValue;
            if (activity.findViewById(result) == null) {
                return result;
            }
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1)) // ! Achtung, hier stand container
                .commit();
        itemSelected = position;
        if (creationFinished) {
            setLayoutManualy();
            // fillControls( itemSelected );
        }
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
