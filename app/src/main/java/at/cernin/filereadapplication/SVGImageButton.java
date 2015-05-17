/*
   Copyright 2013 Paul LeBeau, Cave Rock Software Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package at.cernin.filereadapplication;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * SVGToggleButton is a View widget that allows users to include SVG images in their layouts.
 * 
 * It is implemented as a thin layer over {@code android.widget.ImageView}.
 * <p>
 * In its present form it has one significant limitation.  It uses the {@link SVG#renderToPicture()}
 * method. That means that SVG documents that use {@code <mask>} elements will not display correctly.
 * 
 * @attr ref R.styleable#SVGToggleButton_svg
 */
public class SVGImageButton extends ImageButton
{
   private static Method  setLayerTypeMethod = null;

   {
      try
      {
         setLayerTypeMethod = View.class.getMethod("setLayerType", Integer.TYPE, Paint.class);
      }
      catch (NoSuchMethodException e) { /* do nothing */ }
   }



   public SVGImageButton(Context context)
   {
      super(context);
   }


   public SVGImageButton(Context context, AttributeSet attrs)
   {
      super(context, attrs, 0);
      init(attrs, 0);
   }


   public SVGImageButton(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
      init(attrs, defStyle);
   }

   
   private void  init(AttributeSet attrs, int defStyle)
   {
      if (isInEditMode())
         return;

       /*
       Unklar wozu dieser Codeteil gut ist! Vorerst ausgeklammert!

      TypedArray a = getContext().getTheme()
                     .obtainStyledAttributes(attrs, R.styleable.SVGToggleButton, defStyle, 0);
      try
      {
         int  resourceId = a.getResourceId(R.styleable.SVGToggleButton_svg, -1);
         if (resourceId != -1) {
            setImageResource(resourceId);
            return;
         }

         String  url = a.getString(R.styleable.SVGToggleButton_svg);
         if (url != null)
         {
            Uri  uri = Uri.parse(url);
            if (internalSetImageURI(uri, false))
               return;

            // Last chance, try loading it as an asset filename
            setImageAsset(url);
         }
         
      } finally {
         a.recycle();
      }
      /**/
   }



   /**
    * Directly set the SVG.
    */
   public void  setSVG(SVG mysvg)
   {
      if (mysvg == null)
         throw new IllegalArgumentException("Null value passed to setSVG()");

      setSoftwareLayerType();
      setImageDrawable(new PictureDrawable(mysvg.renderToPicture()));
   }


   /**
    * Load an SVG image from the given resource id.
    */
   @Override
   public void setImageResource(int resourceId)
   {
      new LoadResourceTask().execute(resourceId);
   }


   /**
    * Load an SVG image from the given resource URI.
    */
   @Override
   public void  setImageURI(Uri uri)
   {
      internalSetImageURI(uri, true);
   }


   /**
    * Load an SVG image from the given asset filename.
    */
   public void  setImageAsset(String filename)
   {
      new LoadAssetTask().execute(filename);
   }


   /*
    * Attempt to set a picture from a Uri. Return true if it worked.
    */
   private boolean  internalSetImageURI(Uri uri, boolean isDirectRequestFromUser)
   {
      InputStream  is = null;
      try
      {
         is = getContext().getContentResolver().openInputStream(uri);
      }
      catch (FileNotFoundException e)
      {
         if (isDirectRequestFromUser)
            Log.e("SVGToggleButton", "File not found: " + uri);
         return false;
      }

      new LoadURITask().execute(is);
      return true;
   }


   //===============================================================================================


   private class LoadResourceTask extends AsyncTask<Integer, Integer, Picture>
   {
      protected Picture  doInBackground(Integer... resourceId)
      {
         try
         {
            SVG  svg = SVG.getFromResource(getContext(), resourceId[0]);
            return svg.renderToPicture();
         }
         catch (SVGParseException e)
         {
            Log.e("SVGToggleButton", String.format("Error loading resource 0x%x: %s", resourceId, e.getMessage()));
         }
         return null;
      }

      protected void  onPostExecute(Picture picture)
      {
         if (picture != null) {
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(picture));
         }
      }
   }


   private class LoadAssetTask extends AsyncTask<String, Integer, Picture>
   {
      protected Picture  doInBackground(String... filename)
      {
         try
         {
            SVG  svg = SVG.getFromAsset(getContext().getAssets(), filename[0]);
            /*
             DisplayMetrics dm = getResources().getDisplayMetrics();
             svg.setRenderDPI( dm.densityDpi );
             float ratio = svg.getDocumentAspectRatio(); // width/height

             svg.setDocumentPreserveAspectRatio(PreserveAspectRatio.LETTERBOX);

            if (ratio > 0) {
                float height = getWidth() / ratio;
                // setMinimumHeight((int)height);
                return svg.renderToPicture(((int)getWidth())-10, ((int)height)-10);
            }
            */
            return svg.renderToPicture();

         }
         catch (SVGParseException e)
         {
            Log.e("SVGToggleButton", "Error loading file " + filename + ": " + e.getMessage());
         }
         catch (FileNotFoundException e)
         {
            Log.e("SVGToggleButton", "File not found: " + filename);
         }
         catch (IOException e)
         {
            Log.e("SVGToggleButton", "Unable to load asset file: " + filename, e);
         }
         return null;
      }

      protected void  onPostExecute(Picture picture)
      {
         if (picture != null) {
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(picture));
         }
      }
   }


   private class LoadURITask extends AsyncTask<InputStream, Integer, Picture>
   {
      protected Picture  doInBackground(InputStream... is)
      {
         try
         {
            SVG  svg = SVG.getFromInputStream(is[0]);
            return svg.renderToPicture();
         }
         catch (SVGParseException e)
         {
            Log.e("SVGToggleButton", "Parse error loading URI: " + e.getMessage());
         }
         finally
         {
            try
            {
               is[0].close();
            }
            catch (IOException e) { /* do nothing */ }
         }
         return null;
      }

      protected void  onPostExecute(Picture picture)
      {
         if (picture != null) {
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(picture));
         }
      }
   }


   //===============================================================================================


   /*
    * Use reflection to call an API 11 method from this library (which is configured with a minSdkVersion of 8)
    */
   private void  setSoftwareLayerType()
   {
      if (setLayerTypeMethod == null)
         return;

      try
      {
         int  LAYER_TYPE_SOFTWARE = View.class.getField("LAYER_TYPE_SOFTWARE").getInt(new View(getContext()));
         setLayerTypeMethod.invoke(this, LAYER_TYPE_SOFTWARE, null);
      }
      catch (Exception e)
      {
         Log.w("SVGToggleButton", "Unexpected failure calling setLayerType", e);
      }
   }
}
