package com.tibco.java;

import static com.tibco.be.model.functions.FunctionDomain.ACTION;

import java.util.Date;

import com.tibco.be.model.functions.BEFunction;
import com.tibco.be.model.functions.BEPackage;

@BEPackage(catalog = "Cust", category = "Functions.Java", synopsis = "Utility Functions")
public class JavaFunctions {
	@BEFunction(name = "getSimpleClassName", 
			description="Gets the class name", 
			signature="String getSimpleClassName(Object obj)",
			       freturn = @com.tibco.be.model.functions.FunctionParamDescriptor(name = "", type = "String", desc = "Returns a randomly generated UUID."),
			params = {
					@com.tibco.be.model.functions.FunctionParamDescriptor(name = "obj", type = "Object", desc = "the object of a class"),
					},
			fndomain = {ACTION}, version="1.0",see="",cautions="none", example=" ")
	public static String getSimpleClassName(Object obj) {
		return obj.getClass().getSimpleName();
	}
	
	public static void main(String[] args) {
		Date dt = new Date();
		System.out.println(getSimpleClassName(dt));
	}
}
