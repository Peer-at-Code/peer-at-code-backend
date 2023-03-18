package be.jeffcheasey88.peeratcode.parser.java;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

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
			variable.parse("Testas<Test>t;   ");
			
			assertEquals(0, variable.getModifier());
			assertEquals("Testas<Test>", variable.getType());
			assertEquals("t", variable.getName());
			assertNull(variable.getValue());
		}catch(Exception e){
			fail(e);
		}
	}

	@Test
	void case5(){
		try {
			Variable variable = new Variable();
			variable.parse("  int i,j,k,l=1;   ");
			
			assertEquals(0, variable.getModifier());
			assertEquals("int", variable.getType());
			assertEquals("i", variable.getName());
			assertNull(variable.getValue());
		}catch(Exception e){
			fail(e);
		}
	}
	
	@Test
	void case6(){
		try {
			Class clazz = new Class();
			clazz.parse("public class Test{  int i ,j,k,l=1;  } ");

			List<Variable> vars = clazz.getVariables();
			assertEquals(vars.size(), 4);
			for(int i = 0; i < 3; i++){
				Variable v = vars.get(i);
				assertEquals(0, v.getModifier());
				assertEquals("int", v.getType());
				assertEquals((char)('i'+i), v.getName().charAt(0));
				assertNull(v.getValue());
			}
			Variable v = vars.get(3);
			assertEquals(0, v.getModifier());
			assertEquals("int", v.getType());
			assertEquals('l', v.getName().charAt(0));
			assertEquals("1", ((Value)v.getValue()).value());
		}catch(Exception e){
			fail(e);
		}
	}
	
	@Test
	void case7(){
		try {
			Class clazz = new Class();
			clazz.parse("public class Test{  int i =j=k=l=4;  } ");

			List<Variable> vars = clazz.getVariables();
			assertEquals(vars.size(), 4);
			for(int i = 0; i < 3; i++){
				Variable v = vars.get(i);
				assertEquals(0, v.getModifier());
				assertEquals("int", v.getType());
				assertEquals((char)('i'+i), v.getName().charAt(0));
				assertEquals((char)('i'+i+1), ((Value)v.getValue()).value());
			}
			Variable v = vars.get(3);
			assertEquals(0, v.getModifier());
			assertEquals("int", v.getType());
			assertEquals('l', v.getName().charAt(0));
			assertEquals("4", ((Value)v.getValue()).value());
		}catch(Exception e){
			fail(e);
		}
	}
	
}
