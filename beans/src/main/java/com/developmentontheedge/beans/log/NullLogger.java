package com.developmentontheedge.beans.log;

public class NullLogger implements BeanLogger {
	@Override
	public void warn(String msg) {
	}

	@Override
	public void warn(String msg, Throwable t) {
	}

	@Override
	public void error(String msg) {
	}

	@Override
	public void error(String msg, Throwable t) {
	}
}
