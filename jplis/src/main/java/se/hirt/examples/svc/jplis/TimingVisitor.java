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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The class visitor responsible for setting up the method visitor for classes
 * that should be instrumented.
 * 
 * @author Marcus Hirt
 */
public class TimingVisitor extends ClassVisitor {
	/*
	 * Method name -> TransformDescriptor
	 */
	private final Map<String, List<TransformDescriptor>> transformDescriptors;

	public TimingVisitor(ClassWriter classWriter, List<TransformDescriptor> list) {
		super(Opcodes.ASM6, classWriter);
		this.transformDescriptors = toMap(list);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		TransformDescriptor td = getMatchingDescriptor(name, desc);
		if (td != null) {
			return new TimingMethodAdvisor(td, Opcodes.ASM5, mv, access, name, desc, signature, exceptions);
		}
		return mv;
	}

	private TransformDescriptor getMatchingDescriptor(String name, String desc) {
		List<TransformDescriptor> list = transformDescriptors.get(name);
		if (list != null) {
			for (TransformDescriptor descriptor : list) {
				if (desc.equals(descriptor.getTransformedDescriptor())) {
					return descriptor;
				}
			}
		}
		return null;
	}

	/*
	 * Can be made JDK 7 compatible by rewriting this method...
	 */
	private static Map<String, List<TransformDescriptor>> toMap(List<TransformDescriptor> probes) {
		return probes.stream().collect(Collectors.groupingBy(x -> x.getTransformedMethod()));
	}
}
