package com.allen.agent;

import com.liubs.findinstances.jvmti.InstancesOfClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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
            String methodName = params[3];
            if (threadNum != 0) {
                logger.info("Agent load successfully [threadNum:{} sleepTime:{}s]", threadNum, time);
                runThreadLab(threadNum, time);
            }
            Class[] parameterTypes = {Runnable.class, long.class, long.class, TimeUnit.class};
            invokeTargetMethod(className, methodName, parameterTypes);
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
     * 执行目标类的方法
     *
     * @param targetClassName 目标类
     * @param methodName      目标方法
     * @param args            方法入参
     */
    public static void invokeTargetMethod(String targetClassName, String methodName, Class[] args) {
        Object targetInstance = getTargetInstances(targetClassName);
        try {
            // Runnable.class, long.class, long.class, TimeUnit.class
            Method method = targetInstance.getClass().getMethod(methodName, args);
            Runnable runnable = () -> {
                try {
                    logger.info("开始睡眠～～～～");
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            method.invoke(targetInstance, runnable, 1L, 3L, TimeUnit.SECONDS);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取类的所有对象实例
     *
     * @param className 类对象
     */
    public static Object getTargetInstances(String className) {
        Class targetClass = null;
        List<Class> classes = getLoadedClasses();
        for (Class cls : classes) {
            logger.info("{}", cls.getName());
            if (cls.getName().contains(className)) {
                logger.info("找到目标类 => {}", cls.getName());
                targetClass = cls;
                break;
            }
        }
        if (targetClass == null) {
            logger.error("未找到目标类 [{}]", className);
            throw new RuntimeException("cannot find target class [" + className + "]");
        }
        Object[] instances = InstancesOfClass.getInstances(targetClass);
        Object targetInstance;
        if (instances.length != 0) {
            targetInstance = instances[0];
            logger.info("目标类对象实例 => {}", targetInstance);
            return targetInstance;
        } else {
            logger.error("没有找到目标类{}的对象实例", targetClass.getName());
            throw new RuntimeException("cannot find target object of [" + className + "]");
        }
    }

    /**
     * 从类加载获取加载的类
     *
     * @param classLoader 类加载器
     * @return Vector
     */
    public static Vector<Class> getClassesFromClassLoader(ClassLoader classLoader) {
        try {
            Field classField = ClassLoader.class.getDeclaredField("classes");
            classField.setAccessible(true);

            Vector<Class> classes = (Vector<Class>) classField.get(classLoader);
            if (classes == null) {
                logger.error("[ClassLoader: {}]未获取到类对象列表", classLoader.toString());
            }
            return classes;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class> getLoadedClasses() {
        List<Class> classList = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader != null) {
            logger.info("current classLoader => {}", classLoader);
            Vector<Class> classes = getClassesFromClassLoader(classLoader);
            if (classes != null) {
                classList.addAll(classes);
            }
            classLoader = classLoader.getParent();
        }
        return classList;
    }
}
