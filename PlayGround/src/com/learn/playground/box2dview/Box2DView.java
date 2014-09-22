
package com.learn.playground.box2dview;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.learn.playground.box2dview.InfiniteThread.TickListener;

public class Box2DView extends View implements TickListener {

    public static final float FPS = 60.0f;

    Box2DPhysics physicsHelper;

    private Paint mBallPaint;
    private Paint mSpinnerPaint;
    private Paint mBallTextPaint;

    private RectF viewRect;
    private RectF spinnerRect;
    private RectF ballRect;  

    private ArrayList<Ball> balls;

    private InfiniteThread thread;

    public Box2DView(Context context) {
        super(context);
        init(context);
    }

    public Box2DView(Context context, int initialPosition) {
        super(context);
        init(context);
    }

    public Box2DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Box2DView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        balls = new ArrayList<Ball>();

        for (short i = 0; i < 18; i++) {
            String num = "";
            if (i < 10) {
                num = "0" + i;
            } else {
                num = "" + i;
            }
            balls.add(new Ball(num));
        }

        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mBallPaint.setColor(Color.BLACK);
        mBallPaint.setDither(true);
        mBallPaint.setStyle(Style.FILL);

        mSpinnerPaint = new Paint();
        mSpinnerPaint.setAntiAlias(true);
        mSpinnerPaint.setColor(Color.BLACK);
        mBallPaint.setDither(true);
        mSpinnerPaint.setStyle(Style.FILL);

        mBallTextPaint = new Paint();
        mBallTextPaint.setAntiAlias(true);
        mBallTextPaint.setColor(Color.BLACK);
        mBallTextPaint.setDither(true);
        mBallTextPaint.setStyle(Style.FILL);      
    }

    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
        thread.setRunning(false);
        setup();
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        float side = Math.min(ww, hh);

        float centerX = ww / 2 + xpad / 2;
        float centerY = hh / 2 + ypad / 2;
        float diagonalBy2 = side / 2;

        viewRect = new RectF(centerX - diagonalBy2, centerY - diagonalBy2,
                centerX + diagonalBy2, centerY + diagonalBy2);

        physicsHelper = new Box2DPhysics(viewRect.width(), balls.size());
        float tunnelRadius = physicsHelper.getTunnelRadius();
        spinnerRect = new RectF(centerX - tunnelRadius, centerY
                - tunnelRadius, centerX + tunnelRadius, centerY
                + tunnelRadius);

        ballRect = new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (physicsHelper) {
            World world = physicsHelper.getWorld();

            Body body = world.getBodyList();

            drawMachine(canvas);
            // canvas.drawArc(viewRect, -45, 135, true, mTunnelPaint);
            // canvas.drawCircle(viewRect.centerX(), viewRect.centerY(),
            // physicsHelper.getSpinnerRadius(), mSpinnerPaint);
            int i = balls.size();
            while (body != null) {
                Fixture fixture = body.getFixtureList();
                while (fixture != null) {
                    ShapeType type = fixture.getType();
                    if (type == ShapeType.CIRCLE
                            && body.getType() == BodyType.DYNAMIC) {

                        float centerX = viewRect.left
                                + physicsHelper.getTunnelRadius()
                                + physicsHelper
                                        .getScaledValue(body.getPosition().x);
                        float centerY = (viewRect.top + physicsHelper
                                .getTunnelRadius())
                                - physicsHelper
                                        .getScaledValue(body.getPosition().y);

                        float radius = physicsHelper.getBallRadius();

                        ballRect.left = centerX - radius;
                        ballRect.top = centerY - radius;
                        ballRect.right = centerX + radius;
                        ballRect.bottom = centerY + radius;

                        drawBall(canvas, ballRect, 0, --i);

                    }
                    fixture = fixture.getNext();
                }
                body = body.getNext();
            }
        }

        super.onDraw(canvas);
    }

    public void drawMachine(Canvas canvas) {
    	canvas.drawOval(spinnerRect, mSpinnerPaint);        
    }    

    public void drawBall(Canvas canvas, RectF rect, float angle, int index) {
        if (index >= 0 && index < balls.size()) {
            mBallTextPaint.setTextSize(physicsHelper.getBallRadius() * 0.6f);
            int color = Color.parseColor("#ff"
                    + balls.get(index).getColorCode());

            mBallPaint.setColor(color);

            FontMetrics fm = mBallTextPaint.getFontMetrics();
            float height = -1 * (fm.ascent + fm.descent);
            float width = mBallTextPaint.measureText(balls.get(index)
                    .getNumber());

            //canvas.save(Canvas.MATRIX_SAVE_FLAG);
            //canvas.rotate(-angle, rect.centerX(), rect.centerY());
            canvas.drawOval(rect, mBallPaint);
            canvas.drawText(balls.get(index).getNumber(), rect.centerX()
                    - width / 2f, rect.centerY() + height / 2f, mBallTextPaint);
            //canvas.restore();
        }
    }

    public void reset() {
        synchronized (physicsHelper) {
            physicsHelper.resetWorld();
        }
    }

    public void setup() {

        System.out.println("Animation Started");
        startThread();

    }   

    public void generateBall(Ball b) {
        balls.add(b);
        synchronized (physicsHelper) {
            physicsHelper.generateBall();
        }
    }

    public void update(float dt) {

        if (physicsHelper != null) {
            synchronized (physicsHelper) {
                physicsHelper.update(dt);
            }
        }
    }

    public void startThread() {
        thread = new InfiniteThread(this);
        thread.setRunning(true);
        thread.start();
    }

    public void destroyThread() {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        destroyThread();
        super.onDetachedFromWindow();
    }

    @Override
    public void onTick(float dt) {
        update(dt);
        postInvalidate();
    }
}
