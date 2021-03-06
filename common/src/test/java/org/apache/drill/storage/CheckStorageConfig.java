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
package org.apache.drill.storage;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.logical.LogicalPlan;
import org.apache.drill.common.logical.StoragePluginConfig;
import org.apache.drill.common.util.FileUtils;
import org.apache.drill.common.util.PathScanner;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class CheckStorageConfig {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CheckStorageConfig.class);

  @Test
  public void ensureStorageEnginePickup() {
    Collection<?> engines = PathScanner.scanForImplementations(StoragePluginConfig.class, Lists.newArrayList("org"));
    assertEquals(engines.size(), 1);
  }
  
  @Test
  public void checkPlanParsing() throws Exception{
    LogicalPlan plan = LogicalPlan.parse(DrillConfig.create(), FileUtils.getResourceAsString("/storage_engine_plan.json"));
  }
}
