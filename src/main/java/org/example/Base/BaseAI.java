package org.example.Base;

public abstract class BaseAI {
    private Thread thread;
    private volatile boolean sleep;
    public Thread getThread() {
        return thread;
    }
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }
}
