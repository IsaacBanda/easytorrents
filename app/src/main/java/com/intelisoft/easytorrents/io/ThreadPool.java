package com.intelisoft.easytorrents.io;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Technophile on 6/24/17.
 */

public class ThreadPool {
    private BlockingQueue<Runnable> taskQueue = null;
    private List<Thread> threads = new ArrayList<>();
    private boolean isStopped = false;
    ExecutorService pool;
    private final String TAG = "ThreadPool";

    public ThreadPool(int maxNoOfTasks, int threadPoolSize){
        taskQueue = new ArrayBlockingQueue(maxNoOfTasks);
        pool = Executors.newFixedThreadPool(threadPoolSize);
        new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        }).start();
    }

    public synchronized void  execute(Runnable task) throws Exception{
        if(this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");

        this.taskQueue.add(task);
    }

    public void stop() {
        isStopped = false;
    }

    private void listen() {
        Log.i(TAG, "Waiting on queue");
        while (!isStopped) {
            Runnable runnable = null;
            while ((runnable = taskQueue.poll()) != null) {
                Log.i(TAG, "Executing " + runnable);
                pool.execute(runnable);
            }
        }
        pool.shutdown();
    }
}
