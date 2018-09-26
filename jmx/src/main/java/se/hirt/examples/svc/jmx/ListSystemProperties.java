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
import java.lang.management.RuntimeMXBean;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import se.hirt.examples.svc.attach.util.AttachException;
import se.hirt.examples.svc.attach.util.AttachUtils;

/**
 * Attaches to the JVM with the specified PID, then connects to the local management agent using
 * JMX, and retrieves the system properties from that process over JMX.
 * <p>
 * So why go through all the trouble instead of just using attach? Well, for this example it is
 * certainly overkill. That said JMX is a richer API, containing more information. Also, this
 * example could easily be used across machines, substituting the local serviceURL for a remote one,
 * given that the remote process had the remote JMX management agent up and running.
 * 
 * @author Marcus Hirt
 */
public class ListSystemProperties {
	public static void main(String[] args) throws AttachException, IOException {
		AttachUtils.printJVMVersion();
		String pid = AttachUtils.checkPid(args);
		JMXConnector connector = JMXConnectorFactory.connect(AttachUtils.startLocalAgent(pid));
		RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(),
				ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
		System.out.println(runtimeMXBean.getSystemProperties());
	}
}
