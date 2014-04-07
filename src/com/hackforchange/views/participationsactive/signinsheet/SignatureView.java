package com.hackforchange.views.participationsactive.signinsheet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hackforchange.R;

/**
 * Custom view that allows the user to "write" a signature using their finger. Uses cubic Bezier
 * interpolation for curve smoothing.
 * 
 * <p>Here are two excellent resources:
 * <ul>
 * <li><a href="http://corner.squareup.com/2010/07/smooth-signatures.html">Smooth Signatures</a>
 * <li><a href="http://corner.squareup.com/2012/07/smoother-signatures.html">Smoother Signatures</a>
 * </ul>
 * @author Raj
 */
public class SignatureView extends View{
  private ColorStateList mSignatureColor;
  private int mCurSignatureColor;
  private Paint mSignaturePaint;
  private Path mSignaturePath;
  private final RectF dirtyRectangle;
  private final static float STROKE_WIDTH = 5f;
  private Canvas mCanvas;
  Bitmap mOffScreenBitmap;
  private float mX, mY;
  private boolean mNothingDrawn;

  // stroke width?
  // rotation
  // velocity-based varying stroke width
  // save instance state on rotation

  public SignatureView(Context context, AttributeSet attrs) {
    super(context, attrs);

    setFocusable(true);
    setFocusableInTouchMode(true);

    TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.SignatureView,
            0, 0);

    try {
      mSignatureColor = styledAttrs.getColorStateList(R.styleable.SignatureView_signatureColor);
      setSignatureColor(mSignatureColor == null? ColorStateList.valueOf(Color.BLACK) : mSignatureColor);
    } finally {
      styledAttrs.recycle();
    }

    dirtyRectangle = new RectF();

    init();
  }

  private void init() {
    mSignaturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mSignaturePaint.setColor(mCurSignatureColor);
    mSignaturePaint.setStyle(Paint.Style.STROKE);
    mSignaturePaint.setStrokeCap(Paint.Cap.BUTT);
    mSignaturePaint.setStrokeJoin(Paint.Join.ROUND);
    mSignaturePaint.setStrokeWidth(STROKE_WIDTH);

    mSignaturePath = new Path();

    mNothingDrawn = true;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    if(w==0 || h==0) return;

    super.onSizeChanged(w, h, oldw, oldh);
    createNewBitmap(); //don't put this in the constructor because getWidth and getHeight return 0 before layout is finalized
  }

  private void createNewBitmap() {
    mOffScreenBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mOffScreenBitmap);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawBitmap(mOffScreenBitmap, 0, 0, mSignaturePaint);
  }


  @Override
  public boolean onTouchEvent(MotionEvent e){
    float x = e.getX();
    float y = e.getY();

    switch(e.getAction()){
      case MotionEvent.ACTION_DOWN:
        mNothingDrawn = false;
        handleTouchDown(x, y);
        return true;
      case MotionEvent.ACTION_MOVE:
        handleTouchMove(e);
        break;
      case MotionEvent.ACTION_UP:
        handleTouchUp(x, y);
        break;
      default:
        return false;
    }

    postInvalidate((int) (dirtyRectangle.left - STROKE_WIDTH),
            (int) (dirtyRectangle.top - STROKE_WIDTH),
            (int) (dirtyRectangle.right + STROKE_WIDTH),
            (int) (dirtyRectangle.bottom + STROKE_WIDTH));
    return true;
  }

  private void handleTouchDown(float x, float y) {
    mSignaturePath.reset();
    mSignaturePath.moveTo(x, y); //start a new contour
    mSignaturePath.rLineTo(STROKE_WIDTH, STROKE_WIDTH); //single touches will create dots
    mX = x+STROKE_WIDTH; mY = y+STROKE_WIDTH;
  }

  private void handleTouchMove(MotionEvent e) {
    resetDirtyRectangle(e);
    collectPathHistory(e, mSignaturePath);
    mCanvas.drawPath(mSignaturePath, mSignaturePaint);
  }

  private void resetDirtyRectangle(MotionEvent e) {
    dirtyRectangle.left = Math.min(mX, e.getX());
    dirtyRectangle.right = Math.max(mX, e.getX());
    dirtyRectangle.top = Math.min(mY, e.getY());
    dirtyRectangle.bottom = Math.max(mY, e.getY());
  }

  private void collectPathHistory(MotionEvent e, Path path) {
    if(e.getHistorySize()>1){
      PointF s1 = new PointF(mX, mY);
      PointF s2 = new PointF(e.getHistoricalX(0), e.getHistoricalY(0));
      mSignaturePath.rLineTo(s2.x-mX, s2.y-mY);
      mX = s2.x; mY = s2.y;
      updateDirtyRectangle(s2.x, s2.y);
      PointF controlPoint = s1;

      for(int i=1;i<e.getHistorySize();++i){
        PointF s3 = new PointF(e.getHistoricalX(i), e.getHistoricalY(i));
        controlPoint = calculateBezierControlPoints(s1, s2, s3, controlPoint);

        s1 = s2; s2 = s3; mX = s3.x; mY = s3.y;
        updateDirtyRectangle(s3.x, s3.y);
      }

      mSignaturePath.lineTo(mX, mY); //important! take care of the last point or your lines will turn out shaky
    }
    else{
      mSignaturePath.rLineTo(e.getX()-mX, e.getY()-mY);
      mX = e.getX(); mY = e.getY();
    }
  }

  /**
   * Calculates Bezier control points given three points and then plots the Bezier curve using
   * the {@link android.graphics.Path#cubicTo(float, float, float, float, float, float)} method.
   * 
   * <p>For the algorithm itself, see the <a href="http://www.antigrain.com/research/bezier_interpolation/">
   * the excellent explanation</a> at the Anti-Grain Geometry Project web site. Another good resource is
   * <a href="http://www.benknowscode.com/2012/09/path-interpolation-using-cubic-bezier_9742.html">Ben Olsen's Javascript implementation</a>.
   * <p>Each triplet of points yields two control points. We use one and save the other for the next call to this method.
   * 
   * @param s1 first point
   * @param s2 second point
   * @param s3 third point
   * @param controlPoint control point to use
   * @return control point to be used in next call to this method.
   */
  private PointF calculateBezierControlPoints(PointF s1, PointF s2, PointF s3, PointF controlPoint) {
    Line l1 = new Line(s1, s2);
    Line l2 = new Line(s2, s3);
    PointF m1 = l1.getMidPoint();
    PointF m2 = l2.getMidPoint();
    float k = l2.getLength()/(l1.getLength()+l2.getLength());

    PointF cm = new PointF(m2.x + (m1.x-m2.x)*k, m2.y + (m1.y-m2.y)*k);

    float tx = s2.x - cm.x;
    float ty = s2.y - cm.y;

    PointF c1 = new PointF(m1.x+tx, m1.y+ty);
    PointF c2 = new PointF(m2.x+tx, m2.y+ty);

    mSignaturePath.cubicTo(controlPoint.x, controlPoint.y, c1.x, c1.y, s2.x, s2.y);

    return c2;
  }

  private void updateDirtyRectangle(float x, float y) {
    if(x < dirtyRectangle.left)
      dirtyRectangle.left = x;
    else if(x > dirtyRectangle.right)
      dirtyRectangle.right = x;

    // origin is top-left of screen
    if(y < dirtyRectangle.top)
      dirtyRectangle.top = y;
    else if(y > dirtyRectangle.bottom)
      dirtyRectangle.bottom = y;
  }

  private void handleTouchUp(float x, float y) {
    mSignaturePath.rLineTo(x-mX, y-mY);
    mX = x; mY = y;
    mCanvas.drawPath(mSignaturePath, mSignaturePaint);
    mSignaturePath.reset();
  }

  public final ColorStateList getSignatureColors() {
    return mSignatureColor;
  }

  private static ColorStateList getSignatureColors(Context context, TypedArray attrs) {
    return attrs.getColorStateList(R.styleable.SignatureView_signatureColor);
  }

  public static int getTextColor(Context context,
          TypedArray attrs,
          int def) {
    ColorStateList colors = getSignatureColors(context, attrs);

    if (colors == null) {
      return def;
    } else {
      return colors.getDefaultColor();
    }
  }

  public void setSignatureColor(ColorStateList signatureColor) {
    if(signatureColor==null)
      throw new NullPointerException();

    this.mSignatureColor = signatureColor;
    updateSignatureColor();
  }

  private void updateSignatureColor(){
    boolean inval = false;
    int color = mSignatureColor.getDefaultColor();
    if (color != mCurSignatureColor) {
      mCurSignatureColor = color;
      inval = true;
    }

    if(inval){
      refreshView();
    }
  }

  public void eraseSignature(){
    mNothingDrawn = true;
    mSignaturePath.reset();
    createNewBitmap();
    postInvalidate();
  }

  public Bitmap getSignature(){
    if(mNothingDrawn)
      return null;
    else
      return Bitmap.createScaledBitmap(mOffScreenBitmap, getWidth()/2, getHeight()/2, false);
  }

  private void refreshView(){
    postInvalidate();
    requestLayout();
  }

  /**
   * Models a signature line.
   * @author Raj
   */
  private class Line{
    private PointF left, right;

    public Line(PointF left, PointF right){
      this.left = left;
      this.right = right;
    }

    public float getLength(){
      return (float) Math.sqrt((left.x-right.x)*(left.x-right.x) + (left.y-right.y)*(left.y-right.y));
    }

    public PointF getMidPoint(){
      return new PointF((left.x+right.x)/2, (left.y+right.y)/2);
    }
  }

}
