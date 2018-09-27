package se.hirt.examples.svc.jplis;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

class ExampleAgentTest {

	@Test
	void testReadProbes() throws IOException {
		List<TransformDescriptor> probes = ExampleAgent.readProbes(null);
		assertEquals(3, probes.size());
		for (TransformDescriptor probe : probes) {
			assertEquals(TestProgram.class.getName(), probe.getTransformedClass());
			assertNotNull(probe.getTransformedMethod());
			assertNotNull(probe.getTransformedDescriptor());
		}
	}

}
