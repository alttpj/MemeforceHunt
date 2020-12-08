/*
 * Copyright 2020-2020 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.memeforcehunt.lib.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

public class InstantDeserializerTest extends AbstractJacksonTest {

  @Test
  public void testDeserialze() throws IOException {
    // given
    final InstantDeserializer deserializer = new InstantDeserializer();
    final Instant original = Instant.now();
    when(getParser().getText()).then(args -> original.toString());

    // when
    final Instant deserialize = deserializer.deserialize(getParser(), getContext());

    // then
    assertEquals(original.toString(), deserialize.toString());
  }

}
