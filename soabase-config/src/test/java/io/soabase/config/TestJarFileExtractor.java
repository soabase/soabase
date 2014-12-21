/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.config;

import com.google.common.io.Files;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.File;
import java.nio.charset.Charset;

public class TestJarFileExtractor
{
    @Test
    public void testBasic() throws Exception
    {
        String[] filtered = JarFileExtractor.filter("one", "two", "three");
        Assert.assertEquals(filtered, new String[]{"one", "two", "three"});
    }

    @Test
    public void testResource() throws Exception
    {
        String[] filtered = JarFileExtractor.filter("one", "!package/test-file.txt", "three");
        Assert.assertEquals(filtered.length, 3);
        Assert.assertEquals(filtered[0], "one");
        Assert.assertEquals(filtered[2], "three");

        File file = new File(filtered[1]);
        Assert.assertTrue(file.exists());
        Assert.assertEquals(Files.toString(file, Charset.defaultCharset()), "one\ntwo\nthree\n");
    }

    @Test
    public void testMissingOverride() throws Exception
    {
        String[] filtered = JarFileExtractor.filter("one", "foo/bad/bar/nada/nothing!package/test-file.txt", "three");
        Assert.assertEquals(filtered.length, 3);
        Assert.assertEquals(filtered[0], "one");
        Assert.assertEquals(filtered[2], "three");

        File file = new File(filtered[1]);
        Assert.assertTrue(file.exists());
        Assert.assertEquals(Files.toString(file, Charset.defaultCharset()), "one\ntwo\nthree\n");
    }

    @Test
    public void testOverride() throws Exception
    {
        File tempFile = File.createTempFile("temp", "temp");
        try
        {
            Files.write("override", tempFile, Charset.defaultCharset());

            String[] filtered = JarFileExtractor.filter("one", tempFile.getCanonicalPath() + "!package/test-file.txt", "three");
            File file = new File(filtered[1]);
            Assert.assertTrue(file.exists());
            Assert.assertEquals(file.getCanonicalFile(), tempFile.getCanonicalFile());
            Assert.assertEquals(Files.toString(file, Charset.defaultCharset()), "override");
        }
        finally
        {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
    }
}
