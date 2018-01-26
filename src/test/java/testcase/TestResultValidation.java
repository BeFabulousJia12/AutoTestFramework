package testcase;

import base.PostResult2File;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @Author You Jia
 * @Date 10/16/2017 1:44 PM
 */
public class TestResultValidation {
    @Test
    public static void PostCommandsValidation() throws IOException
    {
        Assert.assertTrue(PostResult2File.readlinesfromtestresultfile("src/test/resources/FailedCase.dat"));
    }

}
