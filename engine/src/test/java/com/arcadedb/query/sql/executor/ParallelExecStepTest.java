/*
 * Copyright © 2021-present Arcade Data Ltd (info@arcadedata.com)
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
 *
 * SPDX-FileCopyrightText: 2021-present Arcade Data Ltd (info@arcadedata.com)
 * SPDX-License-Identifier: Apache-2.0
 */
package com.arcadedb.query.sql.executor;

import com.arcadedb.TestHelper;
import com.arcadedb.database.RID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by luigidellaquila on 26/07/16.
 */
public class ParallelExecStepTest {

  @Test
  public void test() throws Exception {
    TestHelper.executeInNewDatabase((db) -> {
      final CommandContext ctx = new BasicCommandContext();
      final List<InternalExecutionPlan> subPlans = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        final FetchFromRidsStep step0 = new FetchFromRidsStep(Set.of(new RID(db, 12, i)), ctx);
        final FetchFromRidsStep step1 = new FetchFromRidsStep(Set.of(new RID(db, 12, i)), ctx);
        final InternalExecutionPlan plan = new SelectExecutionPlan(ctx);
        plan.getSteps().add(step0);
        plan.getSteps().add(step1);
        subPlans.add(plan);
      }

      final ParallelExecStep step = new ParallelExecStep(subPlans, ctx);

      final SelectExecutionPlan plan = new SelectExecutionPlan(ctx);
      plan.getSteps().add(new FetchFromRidsStep(Set.of(new RID(db, 12, 100)), ctx));
      plan.getSteps().add(step);
      plan.getSteps().add(new FetchFromRidsStep(Set.of(new RID(db, 12, 100)), ctx));
    });
  }
}
