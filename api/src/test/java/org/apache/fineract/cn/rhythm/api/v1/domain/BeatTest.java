/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.rhythm.api.v1.domain;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.fineract.cn.test.domain.ValidationTest;
import org.apache.fineract.cn.test.domain.ValidationTestCase;
import org.junit.runners.Parameterized;

/**
 * @author Myrle Krantz
 */
public class BeatTest extends ValidationTest<Beat> {

  public BeatTest(ValidationTestCase<Beat> testCase) {
    super(testCase);
  }

  @Override
  protected Beat createValidTestSubject() {
    return new Beat("boop", 0);
  }

  @Parameterized.Parameters
  public static Collection testCases() {
    final Collection<ValidationTestCase> ret = new ArrayList<>();
    ret.add(new ValidationTestCase<Beat>("basicCase")
            .adjustment(x -> {})
            .valid(true));
    ret.add(new ValidationTestCase<Beat>("negativeAlignmentHour")
            .adjustment(x -> x.setAlignmentHour(-1))
            .valid(false));
    ret.add(new ValidationTestCase<Beat>("outOfDayAlignmentHour")
            .adjustment(x -> x.setAlignmentHour(24))
            .valid(false));
    return ret;
  }
}
