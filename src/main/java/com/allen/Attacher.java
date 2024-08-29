package com.allen;

import com.sun.tools.attach.VirtualMachine;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: allen
 * @Date: 2022/4/23 16:18
 * @Description:
 **/

public class Attacher {

    private final static Logger logger = LoggerFactory.getLogger(Attacher.class);

    @Option(name = "-f", aliases = "--file", usage = "agent file")
    public String agentFile;

    @Option(name = "-p", aliases = "--pid", usage = "process id")
    public String pid;

    @Option(name = "-n", aliases = "--threadNum", usage = "thread number")
    public String threadNum;

    @Option(name = "-t", aliases = "--sleepTime", required = true, usage = "thread sleep time")
    public int time;

    @Option(name = "-c", aliases = "--class", required = true, usage = "target className")
    public String className;

    public static void main(String[] args) throws Exception {
        System.exit(new Attacher().doMain(args));
    }

    public int doMain(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            String params = threadNum + "-" + time + "-" + className;
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentFile, params);
            return 0;
        } catch (CmdLineException e) {
            logger.error("", e);
            return 1;
        }
    }
}
