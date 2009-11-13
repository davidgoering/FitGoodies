package fitgoodies.util;

import java.lang.reflect.Method;

import fitgoodies.FitGoodiesTestCase;

public final class WaitForResultTest extends FitGoodiesTestCase {
    public static final class Actor {
        private int counter = 0;
        public Boolean myTestMethod() {
            counter++;
            return counter > 11;
        }

		public int getCalls() {
			return counter;
		}

		public Boolean myTestMethodReturnsTrue() { return true; }
        public Boolean myTestMethodReturnsFalse() { return false; }
    }

    public static final class SystemTimeMock implements SystemTime {
    	private long curentSystemTime = 1000000L;
    	@Override
    	public long currentSystemTimeInMS() {
    		return curentSystemTime;
    	}
    	@Override
    	public void sleep(final long sleepTimeInMillis) {
    		curentSystemTime += sleepTimeInMillis;
    	}
    }

    public void testWaitForConstructor() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsTrue", new Class[0]);
        WaitForResult waitForResult = new WaitForResult(method, new Actor(), 0L);
        assertNotNull(waitForResult);
    }

    public void testInvokeMethodReturnsTrue() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsTrue", new Class[0]);
        WaitForResult waitForResult = new WaitForResult(method, new Actor(), 0L);
        waitForResult.invokeMethod();
        assertTrue(waitForResult.lastCallWasSuccessfull());
    }

    public void testInvokeMethodReturnsFalse() throws Exception {
        Method method = Actor.class.getMethod("myTestMethodReturnsFalse", new Class[0]);
        WaitForResult waitForResult = new WaitForResult(method, new WaitForResultTest.Actor(), 0L);
        waitForResult.invokeMethod();
        assertFalse(waitForResult.lastCallWasSuccessfull());
    }

    public void testInvokeWithoutTimeout() throws Exception {
    	Actor actor = new Actor();
        Method method = actor.getClass().getMethod("myTestMethod", new Class[0]);
        final long timeout = 2000L;

        WaitForResult waitForResult =
        	new WaitForResult(method, actor,
        			timeout, new SystemTimeMock());

        waitForResult.setSleepTime(20L);
        waitForResult.repeatInvokeWithTimeout();
        assertEquals(12, actor.getCalls());
        assertEquals(220L, waitForResult.getLastElapsedTime());
        assertTrue(waitForResult.lastCallWasSuccessfull());
    }

    public void testInvokeWithTimeout() throws Exception {
    	Actor actor = new Actor();
        Method method = actor.getClass().getMethod("myTestMethod", new Class[0]);
        final long timeout = 51L;

        WaitForResult waitForResult =
        	new WaitForResult(method, actor,
        			timeout, new SystemTimeMock());

        waitForResult.setSleepTime(10L);
        waitForResult.repeatInvokeWithTimeout();
        assertEquals(7, actor.getCalls());
        assertEquals(60L, waitForResult.getLastElapsedTime());
        assertFalse(waitForResult.lastCallWasSuccessfull());
    }
}