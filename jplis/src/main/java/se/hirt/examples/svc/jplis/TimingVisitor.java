package se.hirt.examples.svc.jplis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
