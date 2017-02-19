package com.developmentontheedge.beans.log;

import java.util.concurrent.Callable;

public class Logger {
	private static BeanLogger defLogger = new NullLogger();
	
	private static ThreadLocal<BeanLogger> log = ThreadLocal.withInitial(() -> defLogger);
	
	public static BeanLogger getLogger()
	{
		return log.get();
	}
	
	public static void setGlobalLogger(BeanLogger logger)
	{
		if(System.getSecurityManager() != null)
		{
			System.getSecurityManager().checkPermission(new RuntimePermission("com.developmentontheedge.beans.setup"));
		}
		defLogger = logger;
	}
	
	public static <V> V withLogger(BeanLogger logger, Callable<V> fn) throws Exception
	{
		BeanLogger oldLogger = log.get();
		log.set(logger);
		try {
			return fn.call();
		}
		finally {
			log.set(oldLogger);
		}
	}
}
