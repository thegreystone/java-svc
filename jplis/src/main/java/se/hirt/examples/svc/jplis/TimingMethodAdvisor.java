package se.hirt.examples.svc.jplis;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * This is the class actually doing the transformation work.
 * 
 * @author Marcus Hirt
 */
public class TimingMethodAdvisor extends AdviceAdapter {
	private final TransformDescriptor transformDescriptor;
	private int localVariableIndexTime = -1;

	public TimingMethodAdvisor(TransformDescriptor transformDescriptor, int asm, MethodVisitor mv, int access,
			String name, String desc, String signature, String[] exceptions) {
		super(asm, mv, access, name, desc);
		this.transformDescriptor = transformDescriptor;
	}

	@Override
	protected void onMethodEnter() {
		super.onMethodEnter();
		emitPlainMessage("Entering " + transformDescriptor.toString());
		emitTimingPrologue();
	}

	@Override
	protected void onMethodExit(int opcode) {
		super.onMethodExit(opcode);
		emitTimingEpilogue();
		emitMessage("Exiting " + transformDescriptor.toString() + " %d ns");
	}

	private void emitPlainMessage(String message) {
		// Getting the static out field
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		// Loading the String message constant to the operand stack
		mv.visitLdcInsn(message);
		// Finally using "out" and the String constant to print out the message
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	/*
	 * Emits a String.format message where %d is the time in nanos.
	 */
	private void emitMessage(String message) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		// Load the message (constant value) on the operand stack
		mv.visitLdcInsn(message);
		// Create an array of size 1
		mv.visitInsn(ICONST_1);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		// Reference to the array is now on stack, will need it twice...
		mv.visitInsn(DUP);
		// When we've retrieved the boxed time, we want to store it in the first array slot (0) 
		mv.visitInsn(ICONST_0);
		// First load the time variable and put it on the operand stack...
		mv.visitVarInsn(LLOAD, localVariableIndexTime);
		// Then box it...
		box(Type.LONG_TYPE);
		// ...then finally store the Long in the first slot of the array, popping one of the array references, the position and the Long reference.
		mv.visitInsn(AASTORE);
		// Now using the message and the remaining object array reference on the stack to do the format, 
		// popping them both, and adding a String.
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format",
				"(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
		// Finally using invoking println on "out" loaded in the first instruction, together with the 
		// string pushed in the previous instruction to print the message
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
	}

	private void emitTimingPrologue() {
		// Invoking nanoTime... 
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
		// ...contructing a new local variable...
		localVariableIndexTime = newLocal(Type.LONG_TYPE);
		// ...and storing the result in it.
		mv.visitVarInsn(LSTORE, localVariableIndexTime);
	}

	private void emitTimingEpilogue() {
		// Invoking nanoTime, putting the result on the stack
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
		// Pushing the nanoTime captured in the beginning of the method body (the prologue) onto the stack
		mv.visitVarInsn(LLOAD, localVariableIndexTime);
		// Now subtracting the top one from the second...
		mv.visitInsn(LSUB);
		// ...and putting the result back into the local variable
		mv.visitVarInsn(LSTORE, localVariableIndexTime);
	}
}
