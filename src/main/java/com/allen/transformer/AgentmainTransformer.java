package com.allen.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:13
 * @Description: instrument类修改demo
 **/
public class AgentmainTransformer implements ClassFileTransformer {
    private final static Logger logger = LoggerFactory.getLogger(AgentmainTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        logger.info("To transform class: " + className);
        return null;
    }
}
