package org.proof.recorder.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

class ProofReflection {
	
	public static class CCException extends Exception {
		/**
		 * 
		 */
		protected CCException() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param detailMessage
		 * @param throwable
		 */
		CCException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param detailMessage
		 */
		protected CCException(String detailMessage) {
			super(detailMessage);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param throwable
		 */
		protected CCException(Throwable throwable) {
			super(throwable);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 3780886235422646676L;
		
	}
	
	/**
	 * Allow for instance call, avoiding certain class circular dependencies. <br />
	 * Calls even private method if java Security allows it.
	 * @param aninstance instance on which method is invoked (if null, static call)
	 * @param classname name of the class containing the method 
	 * (can be null - ignored, actually - if instance if provided, must be provided if static call)
	 * @param amethodname name of the method to invoke
	 * @param parameterTypes array of Classes
	 * @param parameters array of Object
	 * @return resulting Object
	 * @throws CCException if any problem
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object reflectionCall(
			final Object aninstance, 
			final String classname, 
			final String amethodname, 
			final Class[] parameterTypes, 
			final Object[] parameters) throws CCException {
		
	    Object res;// = null;
	    try {
	    	Class aclass;// = null;
	    	if(aninstance == null)
	    	{
	    		aclass = Class.forName(classname);
	    	}
	    	else
	    	{
	    		aclass = aninstance.getClass();
	    	}
	    	
	    //Class[] parameterTypes = new Class[]{String[].class};
	    	
	    final Method amethod = aclass.getDeclaredMethod(amethodname, parameterTypes);
	    	AccessController.doPrivileged(new PrivilegedAction() {
		@Override
		public Object run() {
	                amethod.setAccessible(true);
	                return null; // nothing to return
	    		}
	    	});
	    	res = amethod.invoke(aninstance, parameters);
	    } catch (final ClassNotFoundException e) {
	    	throw new CCException("PROBLEM_TO_ACCESS " + classname + "CLASS", e);
	    } catch (final SecurityException e) {
	    	throw new CCException("PROBLEM_TO_ACCESS " + classname + "#" + amethodname + " METHOD_SECURITY_ISSUE", e);
	    } catch (final NoSuchMethodException e) {
	    	throw new CCException("PROBLEM_TO_ACCESS " + classname + "#" + amethodname + " METHOD_NOT_FOUND", e);
	    } catch (final IllegalArgumentException e) {
			throw new CCException("PROBLEM_TO_ACCESS " + classname + "#" + amethodname + " METHOD_ILLEGAL_ARGUMENTS " + Arrays.toString(parameters), e);
		} catch (final IllegalAccessException e) {
			throw new CCException("PROBLEM_TO_ACCESS " + classname + "#" + amethodname + " METHOD_ACCESS_RESTRICTION", e);
	    } catch (final InvocationTargetException e) {
		throw new CCException("PROBLEM_TO_ACCESS " + classname + "#" + amethodname + " METHOD_INVOCATION_ISSUE", e);
	    } 
	    return res;
	}

}
