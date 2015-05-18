package at.cernin.filereadapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

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
public class SVGImageView extends ImageView {

    public SVG svg = null;



    public SVGImageView(Context context) {
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
        optimizePresentation();

    }

    public void setImageViewRessource(Context context, int resourceId)
            throws SVGParseException {

        svg = SVG.getFromResource(context, resourceId);
        optimizePresentation();

    }

    private void optimizePresentation()
            throws SVGParseException {
        /*
        svg.setDocumentHeight("90%");
        svg.setDocumentWidth("90%");
        svg.setDocumentPreserveAspectRatio(
                new PreserveAspectRatio(PreserveAspectRatio.Alignment.XMidYMid,
                        PreserveAspectRatio.Scale.Meet));
         */
        // svg.setDocumentPreserveAspectRatio( PreserveAspectRatio.LETTERBOX );


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
            canvas.translate(widthDiff, heightDiff);
            svg.renderToCanvas(canvas);
        }
        else {
            super.onDraw(canvas);
        }
    }




}
