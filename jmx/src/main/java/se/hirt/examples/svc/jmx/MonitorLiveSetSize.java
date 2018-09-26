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
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;

import se.hirt.examples.svc.attach.util.AttachUtils;

/**
 * Attaches to the JVM with the specified PID, and starts the local management agent. If successful,
 * the used memory after GC for the process will be tracked and updated after each GC.
 * <p>
 * We're being dirty little hobbitses again, using the HotSpot specific implementation of the
 * GarbageCollectorMXBean, since we want to use the lastGC info to print all the after GC statistics
 * whenever we've had a GC.
 * <p>
 * Of course, this example should never be used as is - young collections can happen very
 * frequently. One should probably be a bit selective about registering notification listeners on
 * them. Or at least post the updates to a separate size limited blocking queue, which can be fully
 * emptied on each update, and only the last update printed. Also, notifications do not come with
 * delivery guarantees - one would probably want to poll the information once in a while for good
 * measure.
 * <p>
 * One way to try out this command even if the process connected to isn't allocating much, is to
 * hook up the JMC MBean browser to the same process, go to the Memory tab and then click the
 * garbage can icon in the upper right corner.
 * 
 * @author Marcus Hirt
 */
@SuppressWarnings("restriction")
public class MonitorLiveSetSize {

	public static void main(String[] args) throws Exception {
		AttachUtils.printJVMVersion();
		String pid = AttachUtils.checkPid(args);
		JMXConnector connector = JMXConnectorFactory.connect(AttachUtils.startLocalAgent(pid));
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		List<GarbageCollectorMXBean> mBeans = getGarbageCollectorMBeans(connection, getGCNames(connection));

		registerNotifications(connection, mBeans);
		printInitGCStats(mBeans);

		System.out.println("Waiting for GC notifications!");
		System.out.println("Press <enter> to exit!");
		System.in.read();
	}

	private static void registerNotifications(MBeanServerConnection connection, List<GarbageCollectorMXBean> mBeans)
			throws InstanceNotFoundException, IOException {
		GCNotificationListener listener = new GCNotificationListener();
		for (GarbageCollectorMXBean mbean : mBeans) {
			connection.addNotificationListener(mbean.getObjectName(), listener, null, mbean);
		}
	}

	private static void printInitGCStats(List<GarbageCollectorMXBean> mBeans) {
		for (GarbageCollectorMXBean mbean : mBeans) {
			printGCStats(mbean);
		}
		printLastGCInfo(mBeans.get(0));
	}

	private static void printGCStats(GarbageCollectorMXBean mBean) {
		System.out.println(String.format("GC: %s\tCount:%3d\tTime:%5d", mBean.getName(), mBean.getCollectionCount(),
				mBean.getCollectionTime()));
	}

	private static void printLastGCInfo(GarbageCollectorMXBean mBean) {
		GcInfo lastGcInfo = mBean.getLastGcInfo();
		for (Entry<String, MemoryUsage> usage : lastGcInfo.getMemoryUsageAfterGc().entrySet()) {
			System.out.println(String.format("%-25s\tused=%9d kB\tmax=%9d kB", usage.getKey() + ":",
					toKB(usage.getValue().getUsed()), toKB(usage.getValue().getMax())));
		}
	}

	private static long toKB(long memory) {
		return memory / 1024;
	}

	private static List<GarbageCollectorMXBean> getGarbageCollectorMBeans(
		MBeanServerConnection connection, Set<ObjectName> gcNames) throws IOException {
		List<GarbageCollectorMXBean> gcBeans = new ArrayList<GarbageCollectorMXBean>(gcNames.size());
		for (ObjectName name : gcNames) {
			gcBeans.add(ManagementFactory.newPlatformMXBeanProxy(connection, name.getCanonicalName(),
					GarbageCollectorMXBean.class));
		}
		return gcBeans;
	}

	private static Set<ObjectName> getGCNames(MBeanServerConnection connection)
			throws MalformedObjectNameException, IOException {
		return connection.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"),
				null);
	}

	private static class GCNotificationListener implements NotificationListener {
		@Override
		public void handleNotification(Notification notification, Object handback) {
			GarbageCollectorMXBean mbean = (GarbageCollectorMXBean) handback;
			printGCStats(mbean);
			printLastGCInfo(mbean);
		}
	}
}
