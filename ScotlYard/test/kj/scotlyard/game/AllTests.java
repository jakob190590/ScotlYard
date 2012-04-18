package kj.scotlyard.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ kj.scotlyard.game.model.AllTests.class, kj.scotlyard.game.rules.AllTests.class, 
		kj.scotlyard.game.util.AllTests.class, kj.scotlyard.game.control.impl.AllTests.class })
public class AllTests {

}
