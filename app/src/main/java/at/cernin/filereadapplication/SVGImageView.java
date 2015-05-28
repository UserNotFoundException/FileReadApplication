package at.cernin.filereadapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
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
    public float documentHeight = 0;
    public float documentWidth = 0;
    public float documentRenderDPI = 0;

    SVGImageListener svgImageListener = null;

    public SVGImageView(Context context) {
        super(context);

        // Umschalten auf Softwarerendering, weil nicht alle SVG-Befehle
        // im Hardwarerendering funktionieren
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            // Do something for HoneyComb and above versions
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            // do something for phones running an SDK before HoneyComb
            //
            // vermutlich nicht nötig, da es vorher kein Hardwarerendering gab
        }
    }

    public void setSVGImageListener( SVGImageListener svgImageListener) {
        this.svgImageListener = svgImageListener;
    }

    private class AssetParams {
        Context context;
        String filename;
        AssetParams(Context context, String filename) {
            this.context = context;
            this.filename = filename;
        }
    }
    private class GetFromAsset extends AsyncTask <AssetParams, Integer, SVG > {
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
            if (svg != null) {
                documentHeight = svg.getDocumentHeight();
                documentWidth = svg.getDocumentWidth();
                documentRenderDPI = svg.getRenderDPI();

                if (svgImageListener != null) {
                    svgImageListener.SVGImageLoaded(SVGImageView.this);
                }
            }
        }
    }

    public void setImageViewAsset(Context context, String filename) {

        new GetFromAsset().executeOnExecutor(
                GetFromAsset.THREAD_POOL_EXECUTOR, new AssetParams(context, filename)
        );

    }


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
            if (svg != null) {
                documentHeight = svg.getDocumentHeight();
                documentWidth = svg.getDocumentWidth();
                documentRenderDPI = svg.getRenderDPI();

                if (svgImageListener != null) {
                    svgImageListener.SVGImageLoaded(SVGImageView.this);
                }
            }
        }
    }

    public void setImageViewRessource(Context context, int resourceId) {

        new GetFromResource().executeOnExecutor(
                GetFromResource.THREAD_POOL_EXECUTOR, new ResourceParams(context, resourceId)
        );

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Recalculate Grafik presentation
        svgImageListener.SVGImageLoaded( SVGImageView.this );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (null != svg) {
            /**/
            float heightDiff = (canvas.getHeight()-svg.getDocumentHeight())/2;
            float widthDiff = (canvas.getWidth()-svg.getDocumentWidth())/2;
            /*
            Log.i("filereadapplication",
                    "Hight: " + svg.getDocumentHeight() + "   Width: " + svg.getDocumentWidth());
            */
            canvas.scale(0.95f, 0.95f, canvas.getWidth() / 2, canvas.getHeight() / 2);
            canvas.translate(widthDiff, heightDiff);

            //svg.setDocumentViewBox(0, 0, svg.getDocumentWidth()/2, svg.getDocumentHeight()/2);
            svg.renderToCanvas(canvas);
        }
        else {
            //super.onDraw(canvas);
        }
    }




}
