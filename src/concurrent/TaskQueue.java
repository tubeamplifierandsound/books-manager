package concurrent;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import interfaces.TaskDelegate;
// Since Java does not have the concept of a delegate, this behavior
//will be implemented using a functional interface


public class TaskQueue {

    private boolean isPoolActive = true;
    private List<Thread> threads;
    private final Queue<TaskDelegate> tasks = new LinkedList<>();
    private Object lockObj = new Object();
    private int threadNum;
    private int taskCounter = 0;

    public TaskQueue(int threadNum){
        this.threadNum = threadNum;
        threads = new ArrayList<Thread>(threadNum);
        for(int i = 0; i < threadNum; i++){
            threads.add(new Thread(()->threadJob()));
            threads.get(i).start();
        }
    }

    private void threadJob(){
        while(isPoolActive){
            DequeueTask();
        }
    }

    private void DequeueTask(){
        TaskDelegate task;
        synchronized(lockObj){
            while(tasks.isEmpty() && isPoolActive){
                try{
                    lockObj.wait();
                }catch(InterruptedException e){
                    // When thread was interrupted - to set interrupt flag for further processing
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if(!isPoolActive){
                return;
            }
            task = tasks.poll();
            if(task != null){
                taskCounter++;
            }
        }
        if(task != null){
            task.run();
        }
        synchronized(lockObj){
            taskCounter--;
            if(taskCounter == 0 && tasks.isEmpty()){
                lockObj.notifyAll();
            }
        }

    }

    public void EnqueueTask(TaskDelegate task){
        synchronized(lockObj){
            tasks.add(task);
            lockObj.notify();
            //taskCounter++;
        }
    }

    public void waitAllTasks(){
        synchronized (lockObj){
            if(taskCounter > 0 || !tasks.isEmpty()){
                try{
                    lockObj.wait();
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void stopPull(){
        synchronized(lockObj){
            isPoolActive = false;
            lockObj.notifyAll();
        }
        for(Thread thread : threads){
            try{
                thread.join();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }


    public int getThreadNum(){
        return threadNum;
    }
}
