package resources;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestingAndExamples {

     class CalculatorService {

        public CalculatorService(){}

        public  int sumService(int a, int b){
            return a+b;
        }

        public  String toString(int a, char b, String c){
            return a+""+b+c;
        }

        public int getint(int var){
            return var;
        }

        public double getdouble(double var){
            return var;
        }

        public boolean getboolean(boolean var){
            return var;
        }

        public Integer getInteger(Integer var){
            return var;
        }

        public Double getDouble(Double var){
            return var;
        }

        public Boolean getBoolean(Boolean var){
            return var;
        }

        public String getString(String var){
            return var;
        }
    }

//--------------------------------------------------------------------------------------------------------------------//

    @Test
    public void testint(){
        CalculatorService cs = new CalculatorService();
        assertEquals(12,cs.getint(2));
    }

    @Test
    public void testInteger(){
        CalculatorService cs = new CalculatorService();
        assertEquals(Integer.valueOf(12),cs.getInteger(2));
    }

    @Test
    public void testdouble(){
        CalculatorService cs = new CalculatorService();
        assertEquals(21.0,cs.getdouble(2));
    }

    @Test
    public void testDouble(){
        CalculatorService cs = new CalculatorService();
        assertEquals(Double.valueOf(12.0),cs.getDouble(2d));
    }

    @Test
    public void testboolean(){
        CalculatorService cs = new CalculatorService();
        assertEquals(true,cs.getboolean(false));
    }

    @Test
    public void testBoolean(){
        CalculatorService cs = new CalculatorService();
        assertEquals(true,cs.getBoolean(false));
    }

    @Test
    public void testString(){
        CalculatorService cs = new CalculatorService();
        assertEquals("55",cs.getString("555"));
    }
    @Test
    public void testNull(){
        CalculatorService cs = new CalculatorService();
        assertNull(cs.getint(1));
    }
    @Test
    public void testNotNull(){
        CalculatorService cs = new CalculatorService();
        assertNotNull(cs.getString(null));
    }
}
