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
package se.hirt.examples.svc.perfcounters.jmx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

import jdk.internal.perf.Perf;
import sun.management.counter.Counter;
import sun.management.counter.perf.PerfInstrumentation;

/**
 * JRockit used to have an MBean exposing internal perf counters. This class provides the same kind
 * of functionality or HotSpot.
 * 
 * @author Marcus Hirt
 */
public class PerfCounters implements DynamicMBean {
	public final static ObjectName MBEAN_OBJECT_NAME;
	public final Map<String, Counter> counterMap;
	private final MBeanInfo info;
	static {
		MBEAN_OBJECT_NAME = createObjectName("se.hirt.management:type=PerfCounters");
	}

	public PerfCounters() {
		counterMap = setUpCounters();
		info = createMBeanInfo();
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name cannot be null."),
					"Cannot invoke getAttribute on " + MBEAN_OBJECT_NAME + " with null as attribute name.");
		}
		Counter c = counterMap.get(attribute);
		if (c == null) {
			throw new AttributeNotFoundException("Could not find the attribute " + attribute);
		}
		return c.getValue();
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		if (attribute == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("The attribute name cannot be null."),
					"Cannot invoke setAttribute on " + MBEAN_OBJECT_NAME + " with null as attribute name.");
		}
		Counter c = counterMap.get(attribute.getName());
		if (c == null) {
			throw new AttributeNotFoundException("Could not find the attribute " + attribute + ".");
		}
		throw new RuntimeOperationsException(new UnsupportedOperationException(),
				"All attributes on the PerfCounters MBean are read only.");
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList attributeList = new AttributeList();
		for (String attribute : attributes) {
			try {
				attributeList.add(new Attribute(attribute, getAttribute(attribute)));
			} catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
				// Seems this one is not supposed to throw exceptions.
			}
		}
		return attributeList;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		// Seems this one is not supposed to throw exceptions.
		// Just ignore.
		return null;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		throw new MBeanException(
				new UnsupportedOperationException(MBEAN_OBJECT_NAME + " does not have any operations."));
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return info;
	}

	private static ObjectName createObjectName(String name) {
		try {
			return new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			// This will not happen – known to be well-formed.
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Counter> setUpCounters() {
		Map<String, Counter> counters = new HashMap<>();
		Perf p = Perf.getPerf();
		try {
			ByteBuffer buffer = p.attach(0, "r");
			PerfInstrumentation perfInstrumentation = new PerfInstrumentation(buffer);
			for (Counter counter : perfInstrumentation.getAllCounters()) {
				counters.put(counter.getName(), counter);
			}
		} catch (IllegalArgumentException | IOException e) {
			System.err.println("Failed to access performance counters. No counters will be available!");
			e.printStackTrace();
		}
		return counters;
	}

	private MBeanInfo createMBeanInfo() {
		Collection<Counter> counters = counterMap.values();
		List<MBeanAttributeInfo> attributes = new ArrayList<>(counters.size());
		for (Counter c : counters) {
			if (!c.isVector()) {
				String typeName = "java.lang.String";
				synchronized (c) {
					Object value = c.getValue();
					if (value != null) {
						typeName = value.getClass().getName();
					}
				}
				attributes.add(new MBeanAttributeInfo(c.getName(), typeName,
						String.format("%s [%s,%s]", c.getName(), c.getUnits(), c.getVariability()), true, false,
						false));
			}
		}
		MBeanAttributeInfo[] attributesArray = attributes.toArray(new MBeanAttributeInfo[attributes.size()]);
		return new MBeanInfo(this.getClass().getName(),
				"An MBean exposing the available JVM Performance Counters as attributes.", attributesArray, null, null,
				null);
	}
}
