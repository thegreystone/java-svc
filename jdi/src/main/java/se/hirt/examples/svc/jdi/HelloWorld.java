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
package se.hirt.examples.svc.jdi;

import java.io.IOException;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.Connector.IntegerArgument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import se.hirt.examples.svc.attach.util.AttachException;
import se.hirt.examples.svc.jdi.util.JdiUtils;

/**
 * Shows how to attach to a JVM with the specified host and port over jdwp.
 * 
 * @author Marcus Hirt
 */
public class HelloWorld {
	public static void main(String[] args) throws AttachException, IOException, IllegalConnectorArgumentsException {
		if (args.length != 2) {
			System.out.println("Must provide host and port!");
			System.exit(2);
		}
		String host = args[0];
		String port = args[1];
		VirtualMachine vm = null;
		try {
			vm = connect(host, port);
			vm.resume();
			System.out.println("The process at " + JdiUtils.prettyPrint(host, port) + " is running " + vm.name()
					+ " version " + vm.version());
			System.out.println("Press <enter> to quit, and to terminate the application connected to.");
			System.in.read();
			vm.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not connect to " + JdiUtils.prettyPrint(host, port));
			System.out.println("Message was: " + e.getMessage() + "\nExiting...");
		}
	}

	private static VirtualMachine connect(String host, String port)
			throws IOException, IllegalConnectorArgumentsException {
		AttachingConnector socketConnector = getSocketConnector();
		Map<String, Argument> arguments = socketConnector.defaultArguments();
		arguments.get("hostname").setValue(host);
		((IntegerArgument) arguments.get("port")).setValue(Integer.parseInt(port));
		return socketConnector.attach(arguments);
	}

	public static AttachingConnector getSocketConnector() {
		for (AttachingConnector connector : Bootstrap.virtualMachineManager().attachingConnectors()) {
			if (connector.name().equals("com.sun.jdi.SocketAttach")) {
				return connector;
			}
		}
		return null;
	}
}
