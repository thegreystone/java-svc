package se.hirt.examples.svc.jmx;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;

import se.hirt.examples.svc.attach.util.AttachException;
import se.hirt.examples.svc.attach.util.AttachUtils;

/**
 * Shows how to execute a Diagnostic Command over JMX. Even though this example
 * behaves just like the attach counterpart, it is remotable by using a remote
 * JMX service URL.
 * <p>
 * Note that the DiagnosticCommand MBean is a dynamic MBean, so no proxy can be
 * created. Also note that we're not really being that dirty little hobbitses -
 * we're using supported APIs only. That said, we should check for the existence
 * of the MBean, and degrade gracefully if not available.
 */
public class ThreadDump {
	private final static String DC_OBJECT_NAME = "com.sun.management:type=DiagnosticCommand";

	public static void main(String[] args) throws IOException, AttachException, InstanceNotFoundException,
			MalformedObjectNameException, MBeanException, ReflectionException {
		AttachUtils.printJVMVersion();
		String pid = AttachUtils.checkPid(args);
		JMXConnector connector = JMXConnectorFactory.connect(AttachUtils.startLocalAgent(pid));

		MBeanServerConnection connection = connector.getMBeanServerConnection();
		String result = (String) connection.invoke(new ObjectName(DC_OBJECT_NAME), "threadPrint",
				new Object[] { new String[0] }, new String[] { String[].class.getName() });
		System.out.println(result);
	}
}
