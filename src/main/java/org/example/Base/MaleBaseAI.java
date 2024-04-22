package org.example.Base;
import org.example.Students.*;
import org.example.Template.*;
public class MaleBaseAI extends BaseAI {
    public MaleBaseAI() {
        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    synchronized (MaleBaseAI.class){
                        while(isSleep()){
                            try {
                                MaleBaseAI.class.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for (Student student : ArraySing.getInstance()){
                        if(student instanceof MaleStudent){
                            student.move(2);
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        setThread(thread);
    }
    public void start() {
        getThread().start();
    }
}
