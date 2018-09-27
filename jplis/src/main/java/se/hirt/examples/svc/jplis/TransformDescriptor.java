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

import java.util.ArrayList;
import java.util.List;

/**
 * Describes where to put the probes.
 * 
 * @author Marcus Hirt
 */
public class TransformDescriptor {
	private final String transformedClass;
	private final String transformedMethod;
	private final String transformedDescriptor;

	/**
	 * Constructor.
	 * 
	 * @param methodDescriptor
	 *            a method descriptor on the format <class>#<method><formal descriptor>.
	 */
	private TransformDescriptor(String methodDescriptor) {
		String[] contents = parse(methodDescriptor);
		transformedClass = contents[0];
		transformedMethod = contents[1];
		transformedDescriptor = contents[2];
	}

	public static List<TransformDescriptor> from(List<String> lines) {
		List<TransformDescriptor> descriptors = new ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("#") || line.trim().isEmpty()) {
				continue;
			}
			descriptors.add(from(line));
		}
		return descriptors;
	}

	/**
	 * Creates a {@link TransformDescriptor}.
	 * 
	 * @param methodDescriptor
	 *            a method descriptor on the format <class>#<method><formal descriptor>.
	 * @return the {@link TransformDescriptor}.
	 */
	public static TransformDescriptor from(String methodDescriptor) {
		return new TransformDescriptor(methodDescriptor);
	}

	/**
	 * @return the class to be transformed.
	 */
	public String getTransformedClass() {
		return transformedClass;
	}

	/**
	 * @return the class to be transformed, in internal form.
	 */
	public String getTransformedClassInternalName() {
		return getTransformedClass().replace('.', '/');
	}

	/**
	 * @return the method to be transformed.
	 */
	public String getTransformedMethod() {
		return transformedMethod;
	}

	/**
	 * @return the formal descriptor of the method to be transformed. See Chapter 4.3.3 of the JVM
	 *         spec.
	 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.3">JVMS
	 *      4.3.3</a>
	 */
	public String getTransformedDescriptor() {
		return transformedDescriptor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transformedClass == null) ? 0 : transformedClass.hashCode());
		result = prime * result + ((transformedDescriptor == null) ? 0 : transformedDescriptor.hashCode());
		result = prime * result + ((transformedMethod == null) ? 0 : transformedMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransformDescriptor other = (TransformDescriptor) obj;
		if (transformedClass == null) {
			if (other.transformedClass != null)
				return false;
		} else if (!transformedClass.equals(other.transformedClass))
			return false;
		if (transformedDescriptor == null) {
			if (other.transformedDescriptor != null)
				return false;
		} else if (!transformedDescriptor.equals(other.transformedDescriptor))
			return false;
		if (transformedMethod == null) {
			if (other.transformedMethod != null)
				return false;
		} else if (!transformedMethod.equals(other.transformedMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getTransformedClass() + "#" + getTransformedMethod() + getTransformedDescriptor();
	}

	private static String[] parse(String methodDescriptor) {
		String[] contents = new String[3];
		int methodIndex = methodDescriptor.indexOf('#');
		contents[0] = methodDescriptor.substring(0, methodIndex);
		int descriptorIndex = methodDescriptor.indexOf('(', methodIndex);
		contents[1] = methodDescriptor.substring(methodIndex + 1, descriptorIndex);
		contents[2] = methodDescriptor.substring(descriptorIndex);
		return contents;
	}
}
