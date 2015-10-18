package test.edu.upenn.cis455;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ServletSupportTest.class, HttpClientTest.class, ParserTest.class,
	XPathEngineImplTest.class})
public class RunAllTests {

}
