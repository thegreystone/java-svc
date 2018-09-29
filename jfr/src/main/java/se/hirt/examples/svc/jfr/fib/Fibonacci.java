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
package se.hirt.examples.svc.jfr.fib;

/**
 * Example class used to demonstrate the JDK Flight Recorder. Calculates the
 * n:th Fibonacci. The events are _not_ generated in here since the examples
 * code should be explicit.
 * 
 * @author Marcus Hirt
 */
public final class Fibonacci {
	/**
	 * Calculates the n:th Fibonacci number recursively.
	 * 
	 * @param n the position in the Fibonacci series to calculate.
	 * @return the value of the n:th Fibonacci number
	 */
	public static long fibonacciRecursive(int n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		}
		return fibonacciRecursive(n - 2) + fibonacciRecursive(n - 1);
	}

	/**
	 * Calculates the n:th Fibonacci number iteratively.
	 * 
	 * @param n the position in the Fibonacci series to calculate.
	 * @return the value of the n:th Fibonacci number
	 */
	public static long fibonacciIterative(int n) {
		long x = 0, y = 1, z = 1;
		for (long i = 0; i < n; i++) {
			x = y;
			y = z;
			z = x + y;
		}
		return x;
	}
}
