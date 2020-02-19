import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test2 {
    @Test
    public void testSolute() throws Exception {
        Solution solution = new Solution();
        Map<List<Integer>, Integer> testcase = new HashMap<>();
        List<Integer> input1 = Arrays.asList(3, 2, 3);
        testcase.put(input1, 3);
        List<Integer> input2 = Arrays.asList(2, 2, 1, 1, 1, 2, 2);
        testcase.put(input2, 2);
        for (List<Integer> key : testcase.keySet()) {
            Integer result = solution.solute(key);
            Assert.assertEquals(result, testcase.get(key));
        }
    }
}
