package kj.scotlyard.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import kj.scotlyard.game.model.AllModelTests;
import kj.scotlyard.game.rules.AllRulesTests;
import kj.scotlyard.game.util.AllUtilTests;

@RunWith(Suite.class)
@SuiteClasses({ AllModelTests.class, AllRulesTests.class, AllUtilTests.class })
public class AllTests {

}
