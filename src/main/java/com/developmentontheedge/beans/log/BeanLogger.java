package com.developmentontheedge.beans.log;

public interface BeanLogger {
	public void warn(String msg);
	public void warn(String msg, Throwable t);
	
	public void error(String msg);
	public void error(String msg, Throwable t);
}
