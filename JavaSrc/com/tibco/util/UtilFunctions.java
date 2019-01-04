package com.tibco.util;

import java.util.UUID;

import com.tibco.be.model.functions.BEFunction;
import com.tibco.be.model.functions.BEPackage;
import static com.tibco.be.model.functions.FunctionDomain.ACTION;

@BEPackage(catalog = "Cust", category = "Functions.Generate", synopsis = "Utility Functions")
public class UtilFunctions {
	
	@BEFunction(name = "UUID", 
			description="Generates a UUID", 
			signature="String UUID()",
			       freturn = @com.tibco.be.model.functions.FunctionParamDescriptor(name = "", type = "String", desc = "Returns a randomly generated UUID."),
			params = {},
			fndomain = {ACTION}, version="1.0",see="",cautions="none", example=" ")
	public static String UUID() {
		return UUID.randomUUID().toString();
	}
	
	public static void main(String[] args) {
		System.out.println(UUID());
	}

}
