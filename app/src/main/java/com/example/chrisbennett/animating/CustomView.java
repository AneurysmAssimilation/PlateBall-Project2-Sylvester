package com.example.chrisbennett.animating;

import java.util.Random;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CustomView extends SurfaceView implements SurfaceHolder.Callback {

    protected Context context;
    private Bitmap balloon;
    private Bitmap bwBalloon;
    DrawingThread thread;
    Paint text;
    int tarX, tarY, upX, upY, downX, downY, rightX, rightY, leftX, leftY, playX, playY; //target-buttons-player
    int lowerBound; //where to stick a line to separate controls
    int width, height;
    int score;
    int diffMultiplyer; //for making it harder
    int t, d; //time for drag randomization and difficulty
    Random rand = new Random();
    Paint tarCol;
    Paint upP; //button color
    Paint downP;
    Paint rightP;
    Paint leftP;
    Paint playP;
    boolean upT, downT, leftT, rightT; //for holding button
    boolean dragUp, dragDown, dragLeft, dragRight; //drag bool
    boolean targetHit; //forscore


    public CustomView(Context ctx, AttributeSet attrs) {
        super(ctx,attrs);
        context = ctx;

        balloon = BitmapFactory.decodeResource(context.getResources(),R.drawable.redballoon);
        bwBalloon=balloon.copy(Bitmap.Config.ARGB_8888, true);
        bwBalloon = resizeBitmap(bwBalloon,75,200);
        /*
        for(int i=0;i<bwBalloon.getWidth();i++) {
           for(int j=0;j<bwBalloon.getHeight();j++) {
                int g = Color.red(bwBalloon.getPixel(i,j));
                bwBalloon.setPixel(i,j,Color.rgb(g,g,g));
            }
        }
        */
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        tarCol = new Paint();
        tarCol.setColor(Color.RED);
        text=new Paint();
        upP = new Paint();
        downP = new Paint();
        rightP = new Paint();
        leftP = new Paint();
        playP = new Paint();
        upP.setColor(Color.WHITE);
        downP.setColor(Color.WHITE);
        rightP.setColor(Color.WHITE);
        leftP.setColor(Color.WHITE);
        playP.setColor(Color.GREEN);
        text.setTextAlign(Paint.Align.LEFT);
        text.setColor(Color.WHITE);
        text.setTextSize(24);
        score = 0;

    }


    public Bitmap resizeBitmap(Bitmap b, int newWidth, int newHeight) {
        int w = b.getWidth();
        int h = b.getHeight();
        width = b.getWidth();
        height = b.getHeight();
        tarX = (w/2)-150;
        tarY = (h/2)-250;
        upX = (w/2)-150;
        upY = h-670;
        downX = (w/2)-150;
        downY = h-550;
        rightX = 700;
        rightY = h-600;
        leftX = 300;
        leftY = h-600;
        playX = (w/2)-150;
        playY = 300;
        lowerBound = 1200;
        float scaleWidth = ((float) newWidth) / w;
        float scaleHeight = ((float) newHeight) / h;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                b, 0, 0, w, h, matrix, false);
        b.recycle();
        return resizedBitmap;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        boolean waitingForDeath = true;
        while(waitingForDeath) {
            try {
                thread.join();
                waitingForDeath = false;
            }
            catch (Exception e) {
                Log.v("Thread Exception", "Waiting on drawing thread to die: " + e.getMessage());
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread= new DrawingThread(holder, context, this);
        thread.setRunning(true);
        thread.start();
    }


    public void customDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawCircle(tarX, tarY, 50, tarCol);
        canvas.drawCircle(upX, upY, 50, upP);//up
        canvas.drawCircle(downX, downY, 50, downP);//down
        canvas.drawCircle(leftX, leftY, 50, leftP);//left
        canvas.drawCircle(rightX, rightY, 50, rightP);//right
        canvas.drawCircle(playX, playY, 50, playP);
        canvas.drawLine(0,lowerBound,width,lowerBound,text);
        canvas.drawText("Score: " + score,600,50,text);
        //x++;

        //Log.v("drawing", "y: " + y);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.v("touch event",event.getX() + "," + event.getY());
        //Log.v("Touch",upX + " | " + upY);
        //double distance = Math.sqrt((x-event.getX()) * (x-event.getX()) + (y-event.getY()) * (y-event.getY()));
        float pX = event.getX();
        float pY = event.getY();

        doTheButtons(pX, pY);

        return true;
    }

    public void doTheButtons(float pX, float pY) {
        CustomView Tcanvas;
        Tcanvas = (CustomView) findViewById(R.id.canvas);

        if(((pX >= upX && pX <= upX + 50) || ((pX <= upX && pX >= upX - 50))) && ((pY >= upY && pY <= upY + 50) || ((pY <= upY && pY >= upY - 50)))) {
            if(!upT) {
                upP.setColor(Color.GRAY);
                upT = true;
                //checkForPoints();
                Tcanvas.invalidate();
            }
            else if(upT) {
                upP.setColor(Color.WHITE);
                upT = false;
                //checkForPoints();
                Tcanvas.invalidate();
            }
        }
        else if(((pX >= downX && pX <= downX + 50) || ((pX <= downX && pX >= downX - 50))) && ((pY >= downY && pY <= downY + 50) || ((pY <= downY && pY >= downY - 50)))) {
            if(!downT) {
                downP.setColor(Color.GRAY);
                downT = true;
                //checkForPoints();
                Tcanvas.invalidate();
            }
            else if(downT) {
                Log.v("T","Down");
                downP.setColor(Color.WHITE);
                downT = false;
                //checkForPoints();
                Tcanvas.invalidate();
            }

        }
        else if(((pX >= rightX && pX <= rightX + 50) || ((pX <= rightX && pX >= rightX - 50))) && ((pY >= rightY && pY <= rightY + 50) || ((pY <= rightY && pY >= rightY - 50)))) {
            if (!rightT) {
                rightP.setColor(Color.GRAY);
                rightT = true;
                //checkForPoints();
                Tcanvas.invalidate();
            } else if (rightT) {
                rightP.setColor(Color.WHITE);
                rightT = false;
                //checkForPoints();
                Tcanvas.invalidate();
            }
        }
        else if(((pX >= leftX && pX <= leftX + 50) || ((pX <= leftX && pX >= leftX - 50))) && ((pY >= leftY && pY <= leftY + 50) || ((pY <= leftY && pY >= leftY - 50)))) {
            if(!leftT) {
                leftP.setColor(Color.GRAY);
                leftT = true;
                //checkForPoints();
                Tcanvas.invalidate();
            }
            else if(leftT) {
                leftP.setColor(Color.WHITE);
                leftT = false;
                //checkForPoints();
                Tcanvas.invalidate();
            }
        }
    }

    void movePlayer() {
        if(upT) { playY -= 15; }
        else if(downT) { playY += 15; }
        else if(leftT) { playX -= 15; }
        else if(rightT) { playX += 15; }
    }

    void checkForPoints() {
        if(((playX + 50 >= tarX && playX + 50 <= tarX + 50) || (playX - 50 <= tarX && playX - 50 >= tarX - 50)) && ((playY + 50 >= tarY && playY + 50 <= tarY + 50) || (playY - 50 <= tarY && playY - 50 >= tarY - 50))) {
            tarCol.setColor(Color.BLUE);
            score++;
        }
        else {
            tarCol.setColor(Color.RED);
        }
    }

    void calculateDrag() {
        int n;

        t++;
        d++;
        if(t == 60) {
            Log.v("move,","v");
            n = rand.nextInt(4);
            if(n==1) { //drag up
                dragUp = true;
                dragDown = false;
                dragLeft = false;
                dragRight = false;
            }
            else if(n==2) { //drag down
                dragUp = false;
                dragDown = true;
                dragLeft = false;
                dragRight = false;
            }
            else if(n==3) { //drag left
                dragUp = false;
                dragDown = false;
                dragLeft = true;
                dragRight = false;
            }
            else if(n==4) {//drag right
                dragUp = false;
                dragDown = false;
                dragLeft = false;
                dragRight = true;
            }
            t = 0;
        }
        if(d >= 200) {
            diffMultiplyer++;
            d=0;
        }

    }

    void applyDrag() {
        if(dragUp) { playY -= 10 + (diffMultiplyer*2); }
        else if(dragDown) { playY += 10 + (diffMultiplyer*2); }
        else if(dragLeft) { playX -= 10 + (diffMultiplyer*2); }
        else if(dragRight) { playX += 10 + (diffMultiplyer*2); }
    }

    void greatWallOfTrump() { //for making you lose when it hits the edge
        if(playX <= 50 || playX >= width-350 || playY <= 50 || playY >= lowerBound-50) {
            Log.v("Lose!","You lose!");
            score = 0;
            t = 0;
            d = 0;
            playX = (width/2) - 150;
            playY = 300;
            dragUp = false;
            dragDown = false;
            dragLeft = false;
            dragRight = false;
        }
    }

    class DrawingThread extends Thread {
        private boolean running;
        private Canvas canvas;
        private SurfaceHolder holder;
        private Context context;
        private CustomView view;

        private int FRAME_RATE = 30;
        private double delay = 1.0 / FRAME_RATE * 1000;
        private long time;

        public DrawingThread(SurfaceHolder holder, Context c, CustomView v) {
            this.holder = holder;
            context = c;
            view = v;
            time = System.currentTimeMillis();
        }

        void setRunning(boolean r) {
            running = r;
        }

        @Override
        public void run() {
            super.run();
            while (running) {
                if (System.currentTimeMillis() - time > delay) {
                    time = System.currentTimeMillis();
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        movePlayer();
                        checkForPoints();
                        calculateDrag();
                        applyDrag();
                        greatWallOfTrump();//border detection
                        view.customDraw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

    }
}
