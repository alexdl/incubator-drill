/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.common.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.drill.common.config.CommonConstants;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.common.exceptions.ExpressionParsingException;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.util.PathScanner;
import org.apache.drill.common.types.Types;

import com.google.common.collect.Lists;

public class FunctionCallFactory {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FunctionCallFactory.class);

  private static Map<String, String> opToFuncTable = new HashMap<>();

  static {
    opToFuncTable.put("+", "add");
    opToFuncTable.put("-", "subtract");
    opToFuncTable.put("/", "divide");
    opToFuncTable.put("*", "multiply");
    opToFuncTable.put("%", "modulo");
    opToFuncTable.put("^", "xor");

    opToFuncTable.put("||", "booleanOr");
    opToFuncTable.put("or", "booleanOr");
    opToFuncTable.put("and", "booleanAnd");
    opToFuncTable.put("&&", "booleanAnd");
    opToFuncTable.put(">", "greater_than");
    opToFuncTable.put("<", "less_than");
    opToFuncTable.put("==", "equal");
    opToFuncTable.put("=", "equal");
    opToFuncTable.put("!=", "not_equal");
    opToFuncTable.put("<>", "not_equal");
    opToFuncTable.put(">=", "greater_than_or_equal_to");
    opToFuncTable.put("<=", "less_than_or_equal_to");
    opToFuncTable.put("is not null", "isnotnull");
    opToFuncTable.put("is true", "istrue");
    
    opToFuncTable.put("!", "not");
    opToFuncTable.put("u-", "negative");
  }

  private static String replaceOpWithFuncName(String op) {
    return (opToFuncTable.containsKey(op)) ? (opToFuncTable.get(op)) : op;
  }

  /*
   * create a cast function.
   * arguments : type -- targetType
   *             ep   -- input expression position
   *             expr -- input expression
   */
  public static LogicalExpression createCast(MajorType type, ExpressionPosition ep, LogicalExpression expr){
    return new CastExpression(expr, type, ep);
  }

  public static LogicalExpression createExpression(String functionName, List<LogicalExpression> args){
    return createExpression(functionName, ExpressionPosition.UNKNOWN, args);
  }

  public static LogicalExpression createExpression(String functionName, ExpressionPosition ep, List<LogicalExpression> args){
    return new FunctionCall(replaceOpWithFuncName(functionName), args, ep);
  }

  public static LogicalExpression createExpression(String unaryName, ExpressionPosition ep, LogicalExpression... e){
    return new FunctionCall(replaceOpWithFuncName(unaryName), Lists.newArrayList(e), ep);
  }

  public static LogicalExpression createByOp(List<LogicalExpression> args, ExpressionPosition ep, List<String> opTypes) {
    if (args.size() == 1) {
      return args.get(0);
    }

    if (args.size() - 1 != opTypes.size())
      throw new DrillRuntimeException("Must receive one more expression then the provided number of operators.");

    LogicalExpression first = args.get(0);
    for (int i = 0; i < opTypes.size(); i++) {
      List<LogicalExpression> l2 = new ArrayList<LogicalExpression>();
      l2.add(first);
      l2.add(args.get(i + 1));
      first = createExpression(opTypes.get(i), ep, args);
    }
    return first;
  }
}
