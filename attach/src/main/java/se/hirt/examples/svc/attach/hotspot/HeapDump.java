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
package se.hirt.examples.svc.attach.hotspot;

import java.io.IOException;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import se.hirt.examples.svc.attach.util.AttachUtils;
import sun.tools.attach.HotSpotVirtualMachine;

/**
 * Attaches to the JVM with the specified PID, and executes a heap dump. Note that 
 * the dump, if not using an absolute path, will be relative to the working directory
 * of the Java process attached to!
 * <p>
 * Note that we're being nasty little hobbitses with the evil downcast.
 * 
 * @author Marcus Hirt
 */
@SuppressWarnings("restriction")
public final class HeapDump {
	public static void main(String[] args) throws AttachNotSupportedException, IOException {
		AttachUtils.printJVMVersion();
		String outFileStr = "dump.hprof";
		if (args.length >= 2) {
			outFileStr = args[1];
		}
		System.out.println("Using dump file: " + outFileStr);
		String pid = AttachUtils.checkPid(args);
		VirtualMachine vm = VirtualMachine.attach(pid);
		HotSpotVirtualMachine hsvm = (HotSpotVirtualMachine) vm;
		String result = AttachUtils.readFromStream(hsvm.dumpHeap(outFileStr));
		System.out.println(result);
		vm.detach();
	}
}
