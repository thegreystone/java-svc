/*
 * Copyright (C) 2018 Marcus Hirt
 *                    www.hirt.se
 *
 * This software is free:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (C) Marcus Hirt, 2018
 */
package se.hirt.examples.svc.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import com.sun.management.OperatingSystemMXBean;

import se.hirt.examples.svc.attach.util.AttachException;
import se.hirt.examples.svc.attach.util.AttachUtils;

/**
 * Attaches to the JVM with the specified PID, and starts the local management agent. If successful,
 * the JMXServiceURL will be used to connect to the locally running JVM, and the CPU load related
 * information will be printed.
 * <p>
 * Note that a JMX connection will only be used if the process trying to connect is running as the
 * same effective user as the process connecting to.
 * <p>
 * Note that we want more information than is in the standard implementation, therefore we are being
 * nasty little hobbitses and rely on the HotSpot implementation rather than the specification. This
 * could of course come back and haunt us in a later release...
 * 
 * @author Marcus Hirt
 */
@SuppressWarnings("restriction")
public class PrintCPULoad {
	public static void main(String[] args) throws IOException, AttachException {
		AttachUtils.printJVMVersion();
		String pid = AttachUtils.checkPid(args);
		JMXConnector connector = JMXConnectorFactory.connect(AttachUtils.startLocalAgent(pid));
		OperatingSystemMXBean osMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(),
				ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
		System.out.println("JVM process load: " + osMXBean.getProcessCpuLoad());
		System.out.println("System load: " + osMXBean.getSystemCpuLoad());
		System.out.println("Process cpu time: " + osMXBean.getProcessCpuTime() / 1000_000.0d + " ms");
	}
}
