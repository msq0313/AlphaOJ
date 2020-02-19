import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test3 {
    @Test
    public void testSolute() throws Exception {
        Solution solution = new Solution();
        Map<Integer, Integer> testcase = new HashMap<Integer, Integer>();
        testcase.put(1, 1);
        testcase.put(-2, 4);
        testcase.put(0, 0);
        testcase.put(4, 16);
        for (Integer key : testcase.keySet()) {
            Integer rst = solution.solute(key);
            Assert.assertEquals(rst, testcase.get(key));
        }
    }
}
