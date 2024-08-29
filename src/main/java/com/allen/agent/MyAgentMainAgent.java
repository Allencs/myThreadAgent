package com.allen.agent;

import com.liubs.findinstances.jvmti.InstancesOfClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Vector;

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
            String className = params[2];
            if (threadNum != 0) {
                logger.info("Agent load successfully [threadNum:{} sleepTime:{}s]", threadNum, time);
                runThreadLab(threadNum, time);
            }
            getTargetInstances(className);
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

    /**
     * 获取类的所有对象实例
     *
     * @param className 类对象
     */
    public static void getTargetInstances(String className) {
//        Class targetClass = null;
//        Vector<Class> classes = getLoadedClasses(Thread.currentThread().getContextClassLoader());
//        for (Class cls : classes) {
//            logger.info("{}", cls.getClass());
//        }
        Object[] instances = InstancesOfClass.getInstances(Thread.class);
        for (Object instance : instances) {
            Thread t = (Thread) instance;
            System.out.println(t.getName());
        }
    }

    /**
     * 获取内存中加载的类
     *
     * @param classLoader 类加载器
     * @return Vector
     */
    public static Vector<Class> getLoadedClasses(ClassLoader classLoader) {
        try {
            Field classField = ClassLoader.class.getDeclaredField("classes");
            classField.setAccessible(true);

            Vector<Class> classes = (Vector<Class>) classField.get(classLoader);
            return classes;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
