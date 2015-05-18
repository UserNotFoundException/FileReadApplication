package at.cernin.filereadapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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

    public SVG svg = null;


    public SVGToggleButton(Context context) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            // Do something for HoneyComb and above versions
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            // do something for phones running an SDK before HoneyComb
        }
    }


    public void setImageViewAsset(Context context, String filename)
            throws IOException, SVGParseException {

        svg = SVG.getFromAsset(context.getAssets(), filename);

    }

    public void setImageViewRessource(Context context, int resourceId)
            throws SVGParseException {

        svg = SVG.getFromResource(context, resourceId);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
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
            canvas.translate(widthDiff, heightDiff);
            svg.renderToCanvas(canvas);
            canvas.restore();
            if (isChecked()) {
                heightDiff = canvas.getHeight()/2;
                widthDiff = canvas.getWidth()/2;
                Paint p = new Paint();
                p.setColor(Color.DKGRAY);
                p.setAlpha(128);
                p.setStrokeWidth(5f);
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
        } else {
            super.onDraw(canvas);
        }
    }




}
