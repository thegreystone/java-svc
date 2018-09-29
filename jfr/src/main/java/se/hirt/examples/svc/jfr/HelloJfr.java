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
package se.hirt.examples.svc.jfr;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jdk.jfr.Event;
import jdk.jfr.Label;

/**
 * Small example program which will emit a "Hello World" event every second.
 * 
 * @author Marcus Hirt
 */
public class HelloJfr {
	private final static ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

	@Label("Hello World!")
	static class HelloWorldEvent extends Event {
		@Label("Message")
		String message;
	}

	private static class EventEmitter implements Runnable {
		@Override
		public void run() {
			HelloWorldEvent event = new HelloWorldEvent();
			event.message = "Hello World!";
			event.commit();	
		}		
	}
	
	public static void main(String... args) throws IOException {
		EXECUTOR.scheduleAtFixedRate(new EventEmitter(), 0, 1, TimeUnit.SECONDS);
		System.out.println("Now emitting an event every second.");
		System.out.println("Press <enter> to quit...");
		System.in.read();
		EXECUTOR.shutdown();
	}
}