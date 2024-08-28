package com.allen;

import com.sun.tools.attach.VirtualMachine;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * @Author: allen
 * @Date: 2022/4/23 16:18
 * @Description:
 **/
public class Attacher {

    @Option(name = "-f", aliases = "--file", usage = "agent file")
    public String agentFile;

    @Option(name = "-p", aliases = "--pid", usage = "process id")
    public String pid;

    @Option(name = "-n", aliases = "--threadNum", usage = "thread number")
    public String threadNum;

    @Option(name = "-t", aliases = "--sleepTime", required = true, usage = "thread sleep time")
    public int time;

    public static void main(String[] args) throws Exception {
        System.exit(new Attacher().doMain(args));
    }

    public int doMain(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            String params = threadNum + "-" + time;
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentFile, params);
            return 0;
        } catch (CmdLineException e) {
            return 1;
        }
    }
}
