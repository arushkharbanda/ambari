/*
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

package org.apache.ambari.server.audit.request.eventcreator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ambari.server.api.services.Request;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.services.ResultStatus;
import org.apache.ambari.server.audit.event.AuditEvent;
import org.apache.ambari.server.audit.event.request.ViewPrivilegeChangeRequestAuditEvent;
import org.apache.ambari.server.controller.internal.ViewPrivilegeResourceProvider;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.orm.entities.PrincipalTypeEntity;

import com.google.common.collect.ImmutableSet;

/**
 * This creator handles view privilege requests
 * For resource type {@link Resource.Type#ViewInstance}
 * and request types {@link Request.Type#PUT}
 */
public class ViewPrivilegeEventCreator implements RequestAuditEventCreator {

  /**
   * Set of {@link Request.Type}s that are handled by this plugin
   */
  private Set<Request.Type> requestTypes = ImmutableSet.<Request.Type>builder().add(Request.Type.PUT).build();

  /**
   * Set of {@link Resource.Type}s that are handled by this plugin
   */
  private Set<Resource.Type> resourceTypes = ImmutableSet.<Resource.Type>builder().add(Resource.Type.ViewPrivilege).build();

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Request.Type> getRequestTypes() {
    return requestTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Resource.Type> getResourceTypes() {
    return resourceTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<ResultStatus.STATUS> getResultStatuses() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuditEvent createAuditEvent(Request request, Result result) {


    Map<String, List<String>> users = getEntities(request, PrincipalTypeEntity.USER_PRINCIPAL_TYPE_NAME);
    Map<String, List<String>> groups = getEntities(request, PrincipalTypeEntity.GROUP_PRINCIPAL_TYPE_NAME);
    Map<String, List<String>> roles = getEntities(request, PrincipalTypeEntity.ROLE_PRINCIPAL_TYPE_NAME);

    return ViewPrivilegeChangeRequestAuditEvent.builder()
      .withTimestamp(System.currentTimeMillis())
      .withRequestType(request.getRequestType())
      .withResultStatus(result.getStatus())
      .withUrl(request.getURI())
      .withRemoteIp(request.getRemoteAddress())
      .withType(RequestAuditEventCreatorHelper.getProperty(request, ViewPrivilegeResourceProvider.PRIVILEGE_VIEW_NAME_PROPERTY_ID))
      .withVersion(RequestAuditEventCreatorHelper.getProperty(request, ViewPrivilegeResourceProvider.PRIVILEGE_VIEW_VERSION_PROPERTY_ID))
      .withName(RequestAuditEventCreatorHelper.getProperty(request, ViewPrivilegeResourceProvider.PRIVILEGE_INSTANCE_NAME_PROPERTY_ID))
      .withUsers(users)
      .withGroups(groups)
      .withRoles(roles)
      .build();

  }

  /**
   * Assembles entities from the request. The result can contain users or groups based on the value of type parameter
   * @param request
   * @param type
   * @return a map of role -> [user|group] names
   */
  private Map<String, List<String>> getEntities(final Request request, final String type) {
    Map<String, List<String>> entities = new HashMap<>();

    for (Map<String, Object> propertyMap : request.getBody().getPropertySets()) {
      String ptype = String.valueOf(propertyMap.get(ViewPrivilegeResourceProvider.PRINCIPAL_TYPE_PROPERTY_ID));
      if (type.equals(ptype)) {
        String role = String.valueOf(propertyMap.get(ViewPrivilegeResourceProvider.PERMISSION_NAME_PROPERTY_ID));
        String name = String.valueOf(propertyMap.get(ViewPrivilegeResourceProvider.PRINCIPAL_NAME_PROPERTY_ID));
        if (!entities.containsKey(role)) {
          entities.put(role, new LinkedList<String>());
        }

        entities.get(role).add(name);
      }
    }
    return entities;
  }

}
