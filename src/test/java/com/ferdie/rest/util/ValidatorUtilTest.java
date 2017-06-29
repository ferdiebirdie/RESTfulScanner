package com.ferdie.rest.util;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorUtilTest {

	@Test
	public void test() {
		String i = "32123";
		Assert.assertTrue(Math.signum(Integer.parseInt(i)) == 1);
		Assert.assertTrue(Math.signum(Long.parseLong(i)) == 1);
	}
	
}
