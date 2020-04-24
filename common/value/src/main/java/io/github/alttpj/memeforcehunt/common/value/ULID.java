// source: https://github.com/azam/ulidj/blob/fe5ee67b891c13199fd7a0c67289d60b44213e73/src/main/java/io/azam/ulidj/ULID.java
/*
 * sulky-modules - several general-purpose modules.
 * Copyright (C) 2007-2019 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2019 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.memeforcehunt.common.value;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/*
 * https://github.com/ulid/spec
 */
@SuppressWarnings("PMD.ShortClassName")
public class ULID {
  private static final char[] ENCODING_CHARS = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
      'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X',
      'Y', 'Z',
  };

  private static final byte[] DECODING_CHARS = {
      // 0
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 8
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 16
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 24
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 32
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 40
      -1, -1, -1, -1, -1, -1, -1, -1,
      // 48
      0, 1, 2, 3, 4, 5, 6, 7,
      // 56
      8, 9, -1, -1, -1, -1, -1, -1,
      // 64
      -1, 10, 11, 12, 13, 14, 15, 16,
      // 72
      17, 1, 18, 19, 1, 20, 21, 0,
      // 80
      22, 23, 24, 25, 26, -1, 27, 28,
      // 88
      29, 30, 31, -1, -1, -1, -1, -1,
      // 96
      -1, 10, 11, 12, 13, 14, 15, 16,
      // 104
      17, 1, 18, 19, 1, 20, 21, 0,
      // 112
      22, 23, 24, 25, 26, -1, 27, 28,
      // 120
      29, 30, 31,
  };

  private static final int MASK = 0x1F;
  private static final int MASK_BITS = 5;
  private static final long TIMESTAMP_OVERFLOW_MASK = 0xFFFF_0000_0000_0000L;
  private static final long TIMESTAMP_MSB_MASK = 0xFFFF_FFFF_FFFF_0000L;
  private static final long RANDOM_MSB_MASK = 0xFFFFL;

  private final Random random;

  public ULID() {
    this(new SecureRandom());
  }

  public ULID(final Random random) {
    Objects.requireNonNull(random, "random must not be null!");
    this.random = random;
  }

  public void appendULID(final StringBuilder stringBuilder) {
    Objects.requireNonNull(stringBuilder, "stringBuilder must not be null!");
    internalAppendULID(stringBuilder, System.currentTimeMillis(), this.random);
  }

  public String nextULID() {
    return nextULID(System.currentTimeMillis());
  }

  public String nextULID(final long timestamp) {
    return internalUIDString(timestamp, this.random);
  }

  public Value nextValue() {
    return nextValue(System.currentTimeMillis());
  }

  public Value nextValue(final long timestamp) {
    return internalNextValue(timestamp, this.random);
  }

  /**
   * Returns the next monotonic value. If an overflow happened while incrementing
   * the random part of the given previous ULID value then the returned value will
   * have a zero random part.
   *
   * @param previousUlid the previous ULID value.
   * @return the next monotonic value.
   */
  public Value nextMonotonicValue(final Value previousUlid) {
    return nextMonotonicValue(previousUlid, System.currentTimeMillis());
  }

  /**
   * Returns the next monotonic value. If an overflow happened while incrementing
   * the random part of the given previous ULID value then the returned value will
   * have a zero random part.
   *
   * @param previousUlid the previous ULID value.
   * @param timestamp    the timestamp of the next ULID value.
   * @return the next monotonic value.
   */
  public Value nextMonotonicValue(final Value previousUlid, final long timestamp) {
    Objects.requireNonNull(previousUlid, "previousUlid must not be null!");
    if (previousUlid.timestamp() == timestamp) {
      return previousUlid.increment();
    }
    return nextValue(timestamp);
  }

  /**
   * Returns the next monotonic value or empty if an overflow happened while incrementing
   * the random part of the given previous ULID value.
   *
   * @param previousUlid the previous ULID value.
   * @return the next monotonic value or empty if an overflow happened.
   */
  public Optional<Value> nextStrictlyMonotonicValue(final Value previousUlid) {
    return nextStrictlyMonotonicValue(previousUlid, System.currentTimeMillis());
  }

  /**
   * Returns the next monotonic value or empty if an overflow happened while incrementing
   * the random part of the given previous ULID value.
   *
   * @param previousUlid the previous ULID value.
   * @param timestamp    the timestamp of the next ULID value.
   * @return the next monotonic value or empty if an overflow happened.
   */
  public Optional<Value> nextStrictlyMonotonicValue(final Value previousUlid, final long timestamp) {
    final Value result = nextMonotonicValue(previousUlid, timestamp);
    if (result.compareTo(previousUlid) < 1) {
      return Optional.empty();
    }
    return Optional.of(result);
  }

  public static Value parseULID(final String ulidString) {
    Objects.requireNonNull(ulidString, "ulidString must not be null!");
    if (ulidString.length() != 26) {
      throw new IllegalArgumentException("ulidString must be exactly 26 chars long.");
    }

    final String timeString = ulidString.substring(0, 10);
    final long time = internalParseCrockford(timeString);
    if ((time & TIMESTAMP_OVERFLOW_MASK) != 0) {
      throw new IllegalArgumentException("ulidString must not exceed '7ZZZZZZZZZZZZZZZZZZZZZZZZZ'!");
    }
    final String part1String = ulidString.substring(10, 18);
    final String part2String = ulidString.substring(18);
    final long part1 = internalParseCrockford(part1String);
    final long part2 = internalParseCrockford(part2String);

    final long most = (time << 16) | (part1 >>> 24);
    final long least = part2 | (part1 << 40);
    return new Value(most, least);
  }

  public static Value fromBytes(final byte[] data) {
    Objects.requireNonNull(data, "data must not be null!");
    if (data.length != 16) {
      throw new IllegalArgumentException("data must be 16 bytes in length!");
    }
    long mostSignificantBits = 0;
    long leastSignificantBits = 0;
    for (int i = 0; i < 8; i++) {
      mostSignificantBits = (mostSignificantBits << 8) | (data[i] & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      leastSignificantBits = (leastSignificantBits << 8) | (data[i] & 0xff);
    }
    return new Value(mostSignificantBits, leastSignificantBits);
  }

  public static class Value
      implements Comparable<Value>, Serializable {
    private static final long serialVersionUID = -3563159514112487717L;

    /*
     * The most significant 64 bits of this ULID.
     */
    private final long mostSignificantBits;

    /*
     * The least significant 64 bits of this ULID.
     */
    private final long leastSignificantBits;

    public Value(final long mostSignificantBits, final long leastSignificantBits) {
      this.mostSignificantBits = mostSignificantBits;
      this.leastSignificantBits = leastSignificantBits;
    }

    /**
     * Returns the most significant 64 bits of this ULID's 128 bit value.
     *
     * @return The most significant 64 bits of this ULID's 128 bit value
     */
    public long getMostSignificantBits() {
      return this.mostSignificantBits;
    }

    /**
     * Returns the least significant 64 bits of this ULID's 128 bit value.
     *
     * @return The least significant 64 bits of this ULID's 128 bit value
     */
    public long getLeastSignificantBits() {
      return this.leastSignificantBits;
    }


    public long timestamp() {
      return this.mostSignificantBits >>> 16;
    }

    public byte[] toBytes() {
      final byte[] result = new byte[16];
      for (int i = 0; i < 8; i++) {
        result[i] = (byte) ((this.mostSignificantBits >> ((7 - i) * 8)) & 0xFF);
      }
      for (int i = 8; i < 16; i++) {
        result[i] = (byte) ((this.leastSignificantBits >> ((15 - i) * 8)) & 0xFF);
      }

      return result;
    }

    public Value increment() {
      final long lsb = this.leastSignificantBits;
      if (lsb != 0xFFFF_FFFF_FFFF_FFFFL) {
        return new Value(this.mostSignificantBits, lsb + 1);
      }
      final long msb = this.mostSignificantBits;
      if ((msb & RANDOM_MSB_MASK) != RANDOM_MSB_MASK) {
        return new Value(msb + 1, 0);
      }
      return new Value(msb & TIMESTAMP_MSB_MASK, 0);
    }

    @Override
    public int hashCode() {
      final long hilo = this.mostSignificantBits ^ this.leastSignificantBits;
      return ((int) (hilo >> 32)) ^ (int) hilo;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      final Value value = (Value) o;

      return this.mostSignificantBits == value.mostSignificantBits
          && this.leastSignificantBits == value.leastSignificantBits;
    }

    @Override
    public int compareTo(final Value val) {
      // The ordering is intentionally set up so that the ULIDs
      // can simply be numerically compared as two numbers
      return (this.mostSignificantBits < val.mostSignificantBits ? -1 :
          (this.mostSignificantBits > val.mostSignificantBits ? 1 :
              (this.leastSignificantBits < val.leastSignificantBits ? -1 :
                  (this.leastSignificantBits > val.leastSignificantBits ? 1 :
                      0))));
    }

    @Override
    public String toString() {
      final char[] buffer = new char[26];

      internalWriteCrockford(buffer, timestamp(), 10, 0);
      long value = ((this.mostSignificantBits & 0xFFFFL) << 24);
      final long interim = (this.leastSignificantBits >>> 40);
      value = value | interim;
      internalWriteCrockford(buffer, value, 8, 10);
      internalWriteCrockford(buffer, this.leastSignificantBits, 8, 18);

      return new String(buffer);
    }
  }

  /*
   * http://crockford.com/wrmg/base32.html
   */
  static void internalAppendCrockford(final StringBuilder builder, final long value, final int count) {
    for (int i = count - 1; i >= 0; i--) {
      final int index = (int) ((value >>> (i * MASK_BITS)) & MASK);
      builder.append(ENCODING_CHARS[index]);
    }
  }

  static long internalParseCrockford(final String input) {
    Objects.requireNonNull(input, "input must not be null!");
    final int length = input.length();
    if (length > 12) {
      throw new IllegalArgumentException("input length must not exceed 12 but was " + length + "!");
    }

    long result = 0;
    for (int i = 0; i < length; i++) {
      final char current = input.charAt(i);
      byte value = -1;
      if (current < DECODING_CHARS.length) {
        value = DECODING_CHARS[current];
      }
      if (value < 0) {
        throw new IllegalArgumentException("Illegal character '" + current + "'!");
      }
      result |= ((long) value) << ((length - 1 - i) * MASK_BITS);
    }
    return result;
  }

  /*
   * http://crockford.com/wrmg/base32.html
   */
  static void internalWriteCrockford(final char[] buffer, final long value, final int count, final int offset) {
    for (int i = 0; i < count; i++) {
      final int index = (int) ((value >>> ((count - i - 1) * MASK_BITS)) & MASK);
      buffer[offset + i] = ENCODING_CHARS[index];
    }
  }

  static String internalUIDString(final long timestamp, final Random random) {
    checkTimestamp(timestamp);

    final char[] buffer = new char[26];

    internalWriteCrockford(buffer, timestamp, 10, 0);
    // could use nextBytes(byte[] bytes) instead
    internalWriteCrockford(buffer, random.nextLong(), 8, 10);
    internalWriteCrockford(buffer, random.nextLong(), 8, 18);

    return new String(buffer);
  }

  static void internalAppendULID(final StringBuilder builder, final long timestamp, final Random random) {
    checkTimestamp(timestamp);

    internalAppendCrockford(builder, timestamp, 10);
    // could use nextBytes(byte[] bytes) instead
    internalAppendCrockford(builder, random.nextLong(), 8);
    internalAppendCrockford(builder, random.nextLong(), 8);
  }

  static Value internalNextValue(final long timestamp, final Random random) {
    checkTimestamp(timestamp);
    // could use nextBytes(byte[] bytes) instead
    long mostSignificantBits = random.nextLong();
    final long leastSignificantBits = random.nextLong();
    mostSignificantBits &= 0xFFFF;
    mostSignificantBits |= (timestamp << 16);
    return new Value(mostSignificantBits, leastSignificantBits);
  }

  private static void checkTimestamp(final long timestamp) {
    if ((timestamp & TIMESTAMP_OVERFLOW_MASK) != 0) {
      throw new IllegalArgumentException("ULID does not support timestamps after +10889-08-02T05:31:50.655Z!");
    }
  }
}
