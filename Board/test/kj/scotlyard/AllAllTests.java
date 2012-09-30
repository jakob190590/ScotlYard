package kj.scotlyard;

import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	kj.scotlyard.board.AllTests.class,
	kj.scotlyard.game.AllTests.class,
	kj.scotlyard.game.control.impl.AllTests.class,
	kj.scotlyard.game.model.AllTests.class,
	kj.scotlyard.game.rules.AllTests.class,
	kj.scotlyard.game.util.AllTests.class })
public class AllAllTests {
	
	static {
		PropertyConfigurator.configure("log4j-test.properties");
	}

}
