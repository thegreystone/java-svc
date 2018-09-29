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
import java.util.function.Function;

import se.hirt.examples.svc.jfr.fib.Fibonacci;
import se.hirt.examples.svc.jfr.fib.FibonacciEvent;
import se.hirt.examples.svc.jfr.util.Utils;

/**
 * Calculates the first 50 fibonacci numbers perpetually, in two separate
 * threads. One using the iterative method, and one using the recursive method.
 * <p>
 * This is a good example to leave running and to connect to, and analyze, using
 * JDK Mission Control.
 * <p>
 * This is a perfect example to run with hsdis - create a template with the
 * events disabled and watch the event generating code be fully optimized away.
 */
public class RunFibonacci {
	private final static ThreadGroup THREAD_GROUP = new ThreadGroup("Fibonacci");

	private static class FibCalculator implements Runnable {
		private final Function<Integer, Long> calculationFunction;
		private final String algorithmName;
		private volatile boolean isRunning = true;

		private FibCalculator(Function<Integer, Long> calculationFunction, String algorithmName) {
			this.calculationFunction = calculationFunction;
			this.algorithmName = algorithmName;
		}

		@Override
		public void run() {
			int n = 0;
			while (isRunning) {
				calculateFibonacci(n++, calculationFunction, algorithmName);
				if (n > 50) {
					n = 0;
				}
				Utils.sleep(500);
			}
		}

		private void shutdown() {
			isRunning = false;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		FibCalculator iterative = new FibCalculator(Fibonacci::fibonacciIterative,
				FibonacciEvent.ALGORITHM_NAME_ITERATIVE);
		FibCalculator recursive = new FibCalculator(Fibonacci::fibonacciRecursive,
				FibonacciEvent.ALGORITHM_NAME_RECURSIVE);
		startThread(iterative);
		startThread(recursive);

		System.out.println("Calculating Fibonacci numbers 0-50 over and over, both recursively and iteratively.");
		System.out.println("Press <enter> to quit");
		System.in.read();
		
		iterative.shutdown();
		recursive.shutdown();
		System.out.println("Shutting down...");
		System.out.println("Done! Bye!");
	}

	private static long calculateFibonacci(int n, Function<Integer, Long> calculationFunction, String algorithmName) {
		FibonacciEvent event = new FibonacciEvent();
		event.begin();
		event.number = n;
		long fibValue = calculationFunction.apply(n);
		event.value = fibValue;
		event.algorithmName = algorithmName;
		event.commit();
		return fibValue;
	}

	private static void startThread(FibCalculator calculator) {
		Thread t = new Thread(THREAD_GROUP, calculator, calculator.algorithmName);
		t.start();
	}
}
