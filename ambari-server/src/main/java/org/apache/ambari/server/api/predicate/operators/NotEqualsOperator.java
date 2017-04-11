/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.server.api.predicate.operators;

import org.apache.ambari.server.controller.predicate.EqualsPredicate;
import org.apache.ambari.server.controller.predicate.NotPredicate;
import org.apache.ambari.server.controller.spi.Predicate;

/**
 * Not Equals relational operator implementation.
 */
public class NotEqualsOperator extends AbstractOperator implements RelationalOperator {

  /**
   * Constructor.
   */
  public NotEqualsOperator() {
    super(0);
  }

  @Override
  public TYPE getType() {
    return TYPE.NOT_EQUAL;
  }

  @Override
  public Predicate toPredicate(String prop, String val) {
    return new NotPredicate(new EqualsPredicate<>(prop, val));
  }

  @Override
  public String getName() {
    return "NotEqualsOperator";
  }
}
