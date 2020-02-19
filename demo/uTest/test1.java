import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class test1 {
    @Test
    public void testSolute() throws Exception {
        Solution solution = new Solution();
        Map<Integer, Boolean> testcase = new HashMap<>();
        testcase.put(121, true);
        testcase.put(-121, false);
        testcase.put(10, false);
        for (Integer key : testcase.keySet()) {
            Boolean result = solution.solute(key);
            Assert.assertEquals(result, testcase.get(key));
        }
    }
}
