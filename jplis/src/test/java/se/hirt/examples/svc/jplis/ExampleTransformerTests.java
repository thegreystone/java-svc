package se.hirt.examples.svc.jplis;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.IllegalClassFormatException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.junit.jupiter.api.Test;

class ExampleTransformerTests {

	@Test
	void testTransform() throws IOException, IllegalClassFormatException {
		List<TransformDescriptor> probes = ExampleAgent.readProbes(null);
		ExampleTransformer transformer = new ExampleTransformer(probes);
		byte[] originalClass = TestUtils.getByteCode(TestProgram.class);
		byte[] transformedClass = transformer.transform(TestProgram.class.getClassLoader(),
				Type.getInternalName(TestProgram.class), TestProgram.class, null, originalClass);
		assertNotNull(transformedClass);
		assertNotEquals(originalClass, transformedClass);

		
		StringWriter writer = new StringWriter();
		TraceClassVisitor visitor = new TraceClassVisitor(new PrintWriter(writer));
		CheckClassAdapter checkAdapter = new CheckClassAdapter(visitor);
		ClassReader reader = new ClassReader(transformedClass);
		reader.accept(checkAdapter, 0);
		String decompiledTransformedClass = writer.getBuffer().toString();
		// System.out.println(decompiledTransformedClass);
		assertNotNull(decompiledTransformedClass);
	}

}
