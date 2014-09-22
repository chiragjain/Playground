
package com.learn.playground.box2dview;


public class InfiniteThread extends Thread {

    // If we are able to get as high as this FPS, don't render again.
    final float FPS = 30;
    private boolean _run = false;

    private boolean RENDER_TIME = false;
    
    private TickListener mListener;

    public InfiniteThread(TickListener listener) {
        this.mListener = listener;
    }

    public void setRunning(boolean run) {
        _run = run;
    }    

    @Override
    public void run() {

        long initialTime = System.nanoTime();
        final double timeF = 1000000000 / FPS;
        double deltaF = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (_run) {

            long currentTime = System.nanoTime();
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if (deltaF >= 1) {
                if (mListener!=null)
                    mListener.onTick(1/FPS);
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                if (RENDER_TIME) {
                    System.out.println(String.format("FPS: %s", frames));
                }
                frames = 0;                
                timer += 1000;
            }
        }
    }
    
    public interface TickListener {
        
        public void onTick(float dt);
    }

}
