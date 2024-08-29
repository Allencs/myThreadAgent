package com.allen.agent;

import com.liubs.findinstances.jvmti.InstancesOfClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:26
 * @Description: 借助Agent将instrument注入到正在运行的JVM中【动态字节码增强，Attach机制】
 **/
public class MyAgentMainAgent {

    private final static Logger logger = LoggerFactory.getLogger(MyAgentMainAgent.class);

    public static void agentmain(String args, Instrumentation inst) {
        try {
            String[] params = args.split("-");
            int threadNum = Integer.parseInt(params[0]);
            int time = Integer.parseInt(params[1]);
            logger.info("Agent load successfully [threadNum:{} sleepTime:{}s]", threadNum, time);
            runThreadLab(threadNum, time);
            getTargetInstances("");
        } catch (Exception e) {
            logger.info("Agent load failed. ", e);
        }
    }

    public static void runThreadLab(int threadNum, int time) {
        logger.info("[OOM caused by too many thread] lab started");
        int i = 0;
        while (i < threadNum) {
            Thread t = new Thread(() -> {
                try {
                    logger.info("{} started", Thread.currentThread().getName());
                    Thread.sleep(time * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            i++;
        }
    }

    public static void getTargetInstances(String className) {
        Object[] instances = InstancesOfClass.getInstances(Thread.class);
        for (Object instance : instances) {
            Thread t = (Thread) instance;
            System.out.println(t.getName());
        }
    }
}
