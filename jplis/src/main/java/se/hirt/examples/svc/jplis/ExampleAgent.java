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
package se.hirt.examples.svc.jplis;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple JPLIS agent timing method invocations for selected methods.
 * 
 * @author Marcus Hirt
 */
public class ExampleAgent {
	private static final String AGENT_VERSION = "v0.0.1";
	private final static String DEFAULT_CONFIG = "default.probes";
	private static Logger LOGGER = Logger.getLogger(ExampleAgent.class.getName());

	/**
	 * This method is run when the agent is started from the command line.
	 *
	 * @param agentArguments
	 *            the arguments to the agent, in this case the path to the config file.
	 * @param instrumentation
	 *            the {@link Instrumentation} instance, provided to us by the kind JVM.
	 */
	public static void premain(String agentArguments, Instrumentation instrumentation) {
		printVersion();
		getLogger().fine("Starting from premain");
		initializeAgent(agentArguments, instrumentation);
	}

	/**
	 * This method is run when the agent is loaded dynamically.
	 *
	 * @param agentArguments
	 *            the arguments to the agent, in this case the path to the config file.
	 * @param instrumentation
	 *            the {@link Instrumentation} instance, provided to us by the kind JVM.
	 */
	public static void agentmain(String agentArguments, Instrumentation instrumentation) {
		printVersion();
		getLogger().fine("Starting from agentmain");
		initializeAgent(agentArguments, instrumentation);
	}

	private static void initializeAgent(String agentArguments, Instrumentation instrumentation) {
		try {
			List<TransformDescriptor> probes = readProbes(agentArguments);
			instrumentation.addTransformer(new ExampleTransformer(probes));
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not initialize agent! Agent will not be active!", e);
		}
	}

	/*
	 * Package private - used in testing...
	 */
	static List<TransformDescriptor> readProbes(String probesFile) throws IOException {
		if (probesFile == null || probesFile.trim().length() == 0) {
			probesFile = DEFAULT_CONFIG;
		}
		File file = new File(probesFile);
		List<String> lines = null;
		if (file.exists()) {
			lines = Files.readAllLines(file.toPath());
		} else {
			lines = Utils.readFromStream(ExampleAgent.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG));
		}
		return TransformDescriptor.from(lines);
	}

	private static void printVersion() {
		getLogger().log(Level.INFO, "AgentExample " + AGENT_VERSION);
	}

	public static Logger getLogger() {
		return LOGGER;
	}
}
