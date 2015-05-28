package at.cernin.filereadapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ToggleButton;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;

/**
 * Created by Walter on 17.05.2015.
 *
 * Eine einfache Klasse, die SVG-Dateinen ohne Zwischenbitmap
 * darstellen kann
 *
 */
public class SVGToggleButton extends ToggleButton {

    // Die im View verwaltete Vektorgrafik
    public SVG svg = null;

    // Der Knopf wird mit quadrischen Markern
    // gezeichnet, die eine Mehrfachauswahlmöglichkeit
    // anzeigen
    public boolean multipleSelection = false;

    SVGButtonListener svgButtonListener = null;


    public SVGToggleButton(Context context) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            // Do something for HoneyComb and above versions
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            // do something for phones running an SDK before HoneyComb
            //
            // vermutlich nicht nötig, da es vorher kein Hardwarerendering gab
        }
    }


    public void setSVGButtonListener( SVGButtonListener svgButtonListener) {
        this.svgButtonListener = svgButtonListener;
    }


    // Assynchroner Aufruf der Asset-Datei-Ladefunktion
    private class AssetParams {
        Context context;
        String filename;
        AssetParams(Context context, String filename) {
            this.context = context;
            this.filename = filename;
        }
    }
    private class GetFromAsset extends AsyncTask<AssetParams, Integer, SVG > {
        protected SVG doInBackground(AssetParams... params) {
            try {
                return SVG.getFromAsset(params[0].context.getAssets(), params[0].filename);
            } catch (SVGParseException e) {
            } catch (IOException e) {
            }
            return null;
        }
        protected void onPostExecute(SVG newsvg) {
            svg = newsvg;
            if (svgButtonListener != null){
                svgButtonListener.SVGImageLoaded( SVGToggleButton.this );
            }
        }
    }

    public void setImageViewAsset(Context context, String filename) {

        new GetFromAsset().executeOnExecutor(
                GetFromAsset.SERIAL_EXECUTOR, new AssetParams(context, filename)
        );

    }

    // Assynchroner Aufruf der Ressourcen-Datei-Ladefunktion
    private class ResourceParams {
        Context context;
        int resourceId;
        ResourceParams(Context context, int resourceId) {
            this.context = context;
            this.resourceId = resourceId;
        }
    }
    private class GetFromResource extends AsyncTask <ResourceParams, Integer, SVG > {
        protected SVG doInBackground(ResourceParams... params) {
            try {
                return SVG.getFromResource(params[0].context, params[0].resourceId);
            } catch (SVGParseException e) {
            }
            return null;
        }
        protected void onPostExecute(SVG newsvg) {
            svg = newsvg;
            if (svgButtonListener != null){
                svgButtonListener.SVGImageLoaded( SVGToggleButton.this );
            }
        }
    }

    public void setImageViewRessource(Context context, int resourceId) {

        new GetFromResource().executeOnExecutor(
                GetFromResource.SERIAL_EXECUTOR, new ResourceParams(context, resourceId)
        );

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // Draw the Frame arroud the Button
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float midX = canvas.getWidth()/2;
        float midY = canvas.getHeight()/2;
        float stroke = 0.05f * Math.min(width, height);


        //canvas.drawColor(Color.WHITE);
        Paint frameP = new Paint();
        // Button with Focus ist Black
        if (this.hasFocus()) {
            frameP.setColor(Color.BLACK);
        } else {
            frameP.setColor(Color.LTGRAY);
        }
        frameP.setStrokeWidth(stroke);

        canvas.drawLine(0, 0, width, 0, frameP);
        canvas.drawLine(width, 0, width, height, frameP);
        canvas.drawLine(width, height, 0, height, frameP);
        canvas.drawLine(0, height, 0, 0, frameP);


        // Draw the Button-Content
        if (null != svg) {
            /**/
            float heightDiff = (canvas.getHeight()-svg.getDocumentHeight())/2;
            float widthDiff = (canvas.getWidth()-svg.getDocumentWidth())/2;

            /*
            RectF box = new RectF(
                    widthDiff, heightDiff,
                    canvas.getWidth()-widthDiff, canvas.getHeight()-heightDiff
            );
             svg.renderToCanvas(canvas, box);
            */
            canvas.save();
            canvas.scale(0.95f, 0.95f, midX, midY);
            canvas.translate(widthDiff, heightDiff);
            svg.renderToCanvas(canvas);
            canvas.restore();

            // Draw the Checkmark on a button with ist switched on
            /*
            if (isChecked()) {
                float Small = 0.8f * Math.min( midX, midY );
                float Diff = 0.3f * Small;

                Paint p = new Paint();
                p.setColor(Color.DKGRAY);
                p.setAlpha(96);
                p.setStrokeWidth(Diff);
                p.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawLine(midX - Small, midY - Small, midX + Small, midY + Small, p);
                canvas.drawLine( midX-Small, midY+Small, midX+Small, midY-Small, p);
                // Momentan Kreuz statt Häckchen
                // Häckchenen als Pfad definieren
                Path path = new Path();
                path.moveTo(0, -7);
                path.lineTo(5.5f, 0);
                //path.arcTo(5, -18, 65, -34, 160, 120, true);
                path.lineTo(20.5f, -15);
                path.lineTo(20, -16);
                //path.arcTo(4, -18, 64, 34, 120, 160, true);
                path.lineTo(6, -1.8f);
                path.lineTo(1.5f, -7.5f);
                path.close();
                path.setFillType(Path.FillType.EVEN_ODD);
                // Skaliere den Pfad auf -1...+1
                Matrix m = new Matrix();
                RectF s = new RectF();
                path.computeBounds(s, true);
                //m.setRectToRect(s, new RectF(-1f, 1f, 1f, -1f), Matrix.ScaleToFit.FILL);
                //path.transform(m);
                // Passe den Pfad an die Canvasgöße an
                float scale = widthDiff < heightDiff ? widthDiff : heightDiff;
                m.reset();
                //m.setScale(scale, scale);
                m.setScale(5, 5);
                m.postTranslate(widthDiff, heightDiff);
                path.transform(m);
                //canvas.drawRect(widthDiff-20, heightDiff-20, widthDiff+20, heightDiff+20, p);
                path.computeBounds(s, true); // Nur zum Debuggen
                canvas.drawPath(path, p);


            }
            */
        } else {
            //super.onDraw(canvas);
        }

        Paint p = new Paint();
        p.setColor(Color.DKGRAY);
        p.setAlpha(96);
        p.setStyle(Paint.Style.STROKE);
        // 0,001 der Breite aber von oben und unten mindestens 5/10 Abstand
        stroke = Math.min(height*0.1f, width * 0.01f);
        p.setStrokeWidth(stroke);
        drawMark(width * 0.07f, midY, 3*stroke, canvas, p);
        drawMark(width * 0.93f, midY, 3*stroke, canvas, p);

    }

    void drawMark( float xPos, float yPos, float size, Canvas canvas, Paint p ) {

        // Draw the Selection-Marks - circle or squere
        if (multipleSelection) {
            RectF r = new RectF(xPos - size, yPos - size,
                    xPos + size, yPos + size
            );
            canvas.drawRect(r, p);
        } else {
            canvas.drawCircle(xPos, yPos, size, p);
        }

        if (isChecked()) {
            p.setStrokeCap(Paint.Cap.ROUND);
            float line = 1.5f * size;
            canvas.drawLine(xPos - line, yPos - line, xPos + line, yPos + line, p);
            canvas.drawLine(xPos - line, yPos + line, xPos + line, yPos - line, p);
        }
    }
}
