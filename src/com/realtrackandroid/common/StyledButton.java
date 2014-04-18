package com.realtrackandroid.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.Button;

public class StyledButton extends Button {
  private final static String DEFAULT_TYPEFACE = "fontawesome";
  final String FONTAWESOME_TTF_PATH = "fontawesome-webfont.ttf";
  private final static LruCache<String, Typeface> TYPEFACE_CACHE = new LruCache<String, Typeface>(10);
  
  public StyledButton(Context context){
    super(context);
    initTypefaceCache();
  }
  
  public StyledButton(Context context, AttributeSet attrSet){
    super(context, attrSet);
    initTypefaceCache();
  }
  
  public StyledButton(Context context, AttributeSet attrSet, int defStyle){
    super(context, attrSet, defStyle);
    initTypefaceCache();
  }

  /**
   * @param typeFaceName name of typeface. Use the same name consistently for your typeface
   * @param typeFacePath must be a path to an asset in your assets folder
   */
  public StyledButton(Context context, String typeFaceName, String typeFacePath){
    super(context);
    initTypefaceCache(typeFaceName, typeFacePath);
  }
  
  private void initTypefaceCache(){
    initTypefaceCache(DEFAULT_TYPEFACE, FONTAWESOME_TTF_PATH);
  }
  
  private void initTypefaceCache(String typeFaceName, String typeFacePath) {
    Typeface typeFace;
    if((typeFace = TYPEFACE_CACHE.get(typeFaceName))==null){
      typeFace = Typeface.createFromAsset(getContext().getAssets(), typeFacePath);
      TYPEFACE_CACHE.put(typeFaceName, typeFace);
    }
    
    setTypeface(typeFace);
  }
}
