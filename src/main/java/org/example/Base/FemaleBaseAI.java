package org.example.Base;
import org.example.Students.Student;
import org.example.Students.FemaleStudent;
import org.example.Template.*;

public class FemaleBaseAI extends BaseAI {
    public FemaleBaseAI() {
        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    synchronized (FemaleBaseAI.class){
                        while(isSleep()){
                            try {
                                FemaleBaseAI.class.wait();
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
                        if(student instanceof FemaleStudent){
                            student.move(2);
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        setThread(thread);
    }

    public void start(){
        getThread().start();
    }

}
