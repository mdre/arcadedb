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

import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.query.sql.parser.DDLStatement;

import java.util.*;

/**
 * @author Luigi Dell'Aquila (luigi.dellaquila-(at)-gmail.com)
 */
public class DDLExecutionPlan implements InternalExecutionPlan {

  private final DDLStatement   statement;
  private final CommandContext context;

  boolean executed = false;

  public DDLExecutionPlan(final CommandContext context, final DDLStatement stm) {
    this.context = context;
    this.statement = stm;
  }

  @Override
  public ResultSet fetchNext(final int n) {
    return new InternalResultSet();
  }

  public void reset(final CommandContext context) {
    executed = false;
  }

  @Override
  public boolean canBeCached() {
    return false;
  }

  public ResultSet executeInternal() throws CommandExecutionException {
    if (executed) {
      throw new CommandExecutionException("Trying to execute a result-set twice. Please use reset()");
    }
    executed = true;
    final ResultSet result = statement.executeDDL(this.context);
    if (result instanceof InternalResultSet set) {
      set.plan = this;
    }
    return result;
  }

  @Override
  public List<ExecutionStep> getSteps() {
    return Collections.emptyList();
  }

  @Override
  public String prettyPrint(final int depth, final int indent) {
    final String spaces = ExecutionStepInternal.getIndent(depth, indent);
    final String result = spaces + "+ DDL\n" + "  " + statement.toString();
    return result;
  }

  @Override
  public Result toResult() {
    final ResultInternal result = new ResultInternal(context.getDatabase());
    result.setProperty("type", "DDLExecutionPlan");
    result.setProperty(JAVA_TYPE, getClass().getName());
    result.setProperty("stmText", statement.toString());
    result.setProperty("cost", getCost());
    result.setProperty("prettyPrint", prettyPrint(0, 2));
    return result;
  }
}
