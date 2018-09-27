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

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Disabled;

/**
 * Small test program giving us something to profile.
 * 
 * @author Marcus Hirt
 */
@Disabled
public final class TestProgram {
	private final static Random RND = new Random();

	public static void main(String[] args) throws IOException {
		Thread t = new Thread(new TestProgram.Loop(), "TestLoop");
		t.setDaemon(true);
		t.start();
		System.out.println("Press <enter> to quit!");
		System.in.read();
	}

	private static class Loop implements Runnable {
		@Override
		public void run() {
			Utils.sleep(2000);
			while (true) {
				doSomething(RND.nextFloat());
				doSomethingElse(String.format("%08xd", RND.nextLong() * 4294967295L));
				Utils.sleep(returnSomething(200 + RND.nextInt(800)));
			}
		}
	}

	private static void doSomething(float nextFloat) {
		Utils.sleep(200);
		System.out.println("Did something! " + nextFloat);
	}

	private static void doSomethingElse(String hexString) {
		Utils.sleep(200);
		System.out.println("Did something else! " + hexString);
	}

	private static long returnSomething(long time) {
		Utils.sleep(time);
		long toReturn = time * 2;
		System.out.println("Got " + time + " returning " + toReturn);
		return toReturn;
	}
}
