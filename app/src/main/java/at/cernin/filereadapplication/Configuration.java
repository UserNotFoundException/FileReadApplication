package at.cernin.filereadapplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Walter on 16.05.2015.
 *
 *
 */
public class Configuration {

    /**
     * Diese Version ist eine Debugversion
     *  Es erfolgen Debugausgaben
     *  Es erfolgt die Datumsüberprüfung und das Beenden der App nach einem Jahr
     */
    public static final boolean DEBUG = true;

    /**
     * Das Produktionsdatum der APP, das zum Jahresvergleich herangezogen wird
     */
    public static final GregorianCalendar ProductionDate = new GregorianCalendar( 2015, 5, 1);


    /**
     * Prüft wenn man im Debug-Modus ist, ob das aktuelle Datum
     * mehr als Jahr vom Produktionsdatum entfernt ist.
     * Wenn das Programm älter ist, dann wird es beendet.
     *
     * Damit wird sicher gestellt, dass Debugversionen
     * nur ein Jahr laufen.
     */
    public static boolean checkDebug( Activity activity ) {

        if (!DEBUG) { return true; }

        if ((System.currentTimeMillis() - 31536000000l) > ProductionDate.getTimeInMillis()) {
            Log.e(activity.getString(R.string.package_name)+":"+activity.getString(R.string.app_name),
                    "Die Probezeit des Programmes ist abgelaufen!"
            );
            activity.finish();
        }
        return false;
    }
}
