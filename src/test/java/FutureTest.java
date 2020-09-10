
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import bgu.spl.mics.Future;

import static org.junit.Assert.*;

public class FutureTest {

     private Future<Object> futureTest;

    @Before
    public void setUp() throws Exception {
        futureTest= new Future<>();
    }

    @After
    public void tearDown() throws Exception {
        futureTest=null;
    }


    @Test
    public void resolve() {
        Object result = new Object();
        futureTest.resolve(result);
        assertEquals("Should return a similar result",result,futureTest.get());
    }

    @Test
    public void isDone() {
        assertFalse(futureTest.isDone());
        Object result = new Object();
        futureTest.resolve(result);
        assertTrue(futureTest.isDone());
    }

    @Test
    public void get1() {
        Object result=  futureTest.get(-5, TimeUnit.SECONDS);
        assertNull(result);


    }


}