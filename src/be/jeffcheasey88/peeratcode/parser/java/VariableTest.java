package be.jeffcheasey88.peeratcode.parser.java;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import be.jeffcheasey88.peeratcode.parser.java.Variable.Value;

class VariableTest {
	
	//int i = 4;
	//int i,j,k,l=1;
	//int lm      ;
	//public static int l;
	//Test<Test>t;
	//Test<Test,K,L>         j = new Test().schedule(p -> { return true;});
	//int i =j=k=l=4;

	@Test
	void case1(){
		try {
			Variable variable = new Variable();
			variable.parse("    int       i    =     4       ;                          ");
			
			assertEquals(0, variable.getModifier());
			assertEquals("int", variable.getType());
			assertEquals("i", variable.getName());
			assertEquals("4", ((Value)variable.getValue()).value());
		}catch(Exception e){
			fail(e);
		}
	}
	
	@Test
	void case2(){
		try {
			Variable variable = new Variable();
			variable.parse("public    static   int    l         ;       ");
			
			assertEquals(JavaParser.getModifier("public")+JavaParser.getModifier("static"), variable.getModifier());
			assertEquals("int", variable.getType());
			assertEquals("l", variable.getName());
			assertNull(variable.getValue());
		}catch(Exception e){
			fail(e);
		}
	}
	
	@Test
	void case3(){
		try {
			Variable variable = new Variable();
			variable.parse(" int lm      ;           ");
			
			assertEquals(0, variable.getModifier());
			assertEquals("int", variable.getType());
			assertEquals("lm", variable.getName());
			assertNull(variable.getValue());
		}catch(Exception e){
			fail(e);
		}
	}
	
	@Test
	void case4(){
		try {
			Variable variable = new Variable();
			variable.parse("Test<Test>t;   ");
			
			assertEquals(0, variable.getModifier());
			assertEquals("Test<Test>", variable.getType());
			assertEquals("t", variable.getName());
			assertNull(variable.getValue());
		}catch(Exception e){
			fail(e);
		}
	}
}
