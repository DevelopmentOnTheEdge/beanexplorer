package com.developmentontheedge.beans.log;

public class ExceptionalLogger implements BeanLogger {
	@Override
	public void warn(String msg) {
	}

	@Override
	public void warn(String msg, Throwable t) {
	}

	@Override
	public void error(String msg) {
		throw new BeanException(msg);
	}

	@Override
	public void error(String msg, Throwable t) {
		throw new BeanException(msg, t);
	}

}
