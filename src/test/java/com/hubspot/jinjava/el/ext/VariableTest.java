/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hubspot.jinjava.el.ext;

import com.google.common.collect.ImmutableMap;
import com.hubspot.jinjava.Jinjava;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class VariableTest {

    private Jinjava jinjava;
    private Map<String, Object> context;

    @Before
    public void setup() {
        jinjava = new Jinjava();
        context = ImmutableMap.of("theString", "theSimpleString");
    }

    @Test
    public void testVariable() {
        assertThat(jinjava.render("{{var:theString:\"Test Store\"}}", context)).isEqualTo("theSimpleString");
    }

    @Test
    public void testDefaultVariable() {
        assertThat(jinjava.render("{{var:theString1:\"Test Store\"}}", context)).isEqualTo("Test Store");
    }
}
