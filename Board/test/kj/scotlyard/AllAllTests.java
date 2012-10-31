package kj.scotlyard;

import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	kj.scotlyard.board.AllTests.class,
	kj.scotlyard.game.AllTests.class })
public class AllAllTests {

}
