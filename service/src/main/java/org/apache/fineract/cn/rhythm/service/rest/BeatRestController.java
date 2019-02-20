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
package org.apache.fineract.cn.rhythm.service.rest;

import static org.apache.fineract.cn.lang.config.TenantHeaderFilter.TENANT_HEADER;

import java.util.List;
import javax.validation.Valid;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.apache.fineract.cn.rhythm.api.v1.domain.Beat;
import org.apache.fineract.cn.rhythm.service.internal.command.CreateBeatCommand;
import org.apache.fineract.cn.rhythm.service.internal.command.DeleteBeatCommand;
import org.apache.fineract.cn.rhythm.service.internal.service.BeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/applications/{applicationidentifier}/beats")
public class BeatRestController {

  private final CommandGateway commandGateway;
  private final BeatService beatService;

  @Autowired
  public BeatRestController(final CommandGateway commandGateway,
                            final BeatService beatService) {
    super();
    this.commandGateway = commandGateway;
    this.beatService = beatService;
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
          method = RequestMethod.GET,
          consumes = MediaType.ALL_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  List<Beat> getAllBeatsForApplication(
          @RequestHeader(TENANT_HEADER) final String tenantIdentifier,
          @PathVariable("applicationidentifier") final String applicationIdentifier) {
    return this.beatService.findAllEntities(tenantIdentifier, applicationIdentifier);
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
          value = "/{beatidentifier}",
          method = RequestMethod.GET,
          consumes = MediaType.ALL_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Beat> getBeat(
          @RequestHeader(TENANT_HEADER) final String tenantIdentifier,
          @PathVariable("applicationidentifier") final String applicationIdentifier,
          @PathVariable("beatidentifier") final String beatIdentifier) {
    return this.beatService.findByIdentifier(tenantIdentifier, applicationIdentifier, beatIdentifier)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> ServiceException
                .notFound("Instance with identifier ''" + applicationIdentifier + "'' doesn''t exist."));
  }

  @Permittable(value = AcceptedTokenType.SYSTEM, permittedEndpoint = "/applications/{applicationidentifier}/beats", acceptTokenIntendedForForeignApplication = true) //Allow apps to use this endpoint in their provisioning code.
  @RequestMapping(
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createBeat(
          @RequestHeader(TENANT_HEADER) final String tenantIdentifier,
          @PathVariable("applicationidentifier") final String applicationIdentifier,
          @RequestBody @Valid final Beat instance) throws InterruptedException {
    this.commandGateway.process(new CreateBeatCommand(tenantIdentifier, applicationIdentifier, instance));
    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
          value = "/{beatidentifier}",
          method = RequestMethod.DELETE,
          consumes = MediaType.ALL_VALUE,
          produces = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteBeat(
          @RequestHeader(TENANT_HEADER) final String tenantIdentifier,
          @PathVariable("applicationidentifier") final String applicationIdentifier,
          @PathVariable("beatidentifier") final String beatIdentifier) throws InterruptedException {
    this.commandGateway.process(new DeleteBeatCommand(tenantIdentifier, applicationIdentifier, beatIdentifier));
    return ResponseEntity.accepted().build();
  }
}
