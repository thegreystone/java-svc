package se.hirt.examples.svc.jplis;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Byte code transformer that will add timing to the method defined by the probes list.
 * 
 * @author Marcus Hirt
 */
public class ExampleTransformer implements ClassFileTransformer {
	private Map<String, List<TransformDescriptor>> probes = new HashMap<>();

	public ExampleTransformer(List<TransformDescriptor> probes) {
		this.probes = toMap(probes);
	}

	@Override
	public byte[] transform(
		ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
		byte[] classfileBuffer) throws IllegalClassFormatException {
		if (probes.containsKey(className)) {
			try {
				ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				TimingVisitor visitor = new TimingVisitor(classWriter, probes.get(className));
				ClassReader reader = new ClassReader(classfileBuffer);
				reader.accept(visitor, 0);
				return classWriter.toByteArray();
			} catch (Throwable t) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"Failed to instrument " + probes.get(className), t);
				return classfileBuffer;
			}
		} else {
			return classfileBuffer;
		}
	}

	/*
	 * Can be made JDK 7 compatible by rewriting this method...
	 */
	private static Map<String, List<TransformDescriptor>> toMap(List<TransformDescriptor> probes) {
		return probes.stream().collect(Collectors.groupingBy(x -> x.getTransformedClassInternalName()));
	}
}
