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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;

/**
 * 
 * @author Marcus Hirt
 */
@Disabled
public class TestUtils {
	public static byte[] getByteCode(Class<?> c) throws IOException {
		return readFully((InputStream) c.getClassLoader().getResourceAsStream(c.getName().replace('.', '/') + ".class"),
				-1, true);
	}
	public static byte[] readFully(InputStream is, int length, boolean readAll) throws IOException {
		try {
			byte[] output = {};
			if (length == -1) {
				length = Integer.MAX_VALUE;
			}
			int pos = 0;
			while (pos < length) {
				int bytesToRead;
				if (pos >= output.length) {
					bytesToRead = Math.min(length - pos, output.length + 1024);
					if (output.length < pos + bytesToRead) {
						output = Arrays.copyOf(output, pos + bytesToRead);
					}
				} else {
					bytesToRead = output.length - pos;
				}
				int cc = is.read(output, pos, bytesToRead);
				if (cc < 0) {
					if (readAll && length != Integer.MAX_VALUE) {
						throw new EOFException("Detect premature EOF");
					} else {
						if (output.length != pos) {
							output = Arrays.copyOf(output, pos);
						}
						break;
					}
				}
				pos += cc;
			}
			return output;
		} finally {
			is.close();
		}
	}
}
