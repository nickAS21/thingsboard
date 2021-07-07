/**
 * Copyright © 2016-2020 The Thingsboard Authors
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
package org.thingsboard.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.lwm2m.LwM2mObject;
import org.thingsboard.server.common.data.lwm2m.ServerSecurityConfig;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;

@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class DeviceLwm2mController extends BaseController {


    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/lwm2m/deviceProfile/{objectIds}", method = RequestMethod.GET)
    @ResponseBody
    public List<LwM2mObject> getLwm2mListObjects(@PathVariable("objectIds") int[] objectIds) throws ThingsboardException {
        try {
            return lwM2MModelsRepository.getLwm2mObjects(objectIds, null);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/lwm2m/deviceProfile/objects", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<LwM2mObject> getLwm2mListObjects(@RequestParam int pageSize,
                                                     @RequestParam int page,
                                                     @RequestParam(required = false) String textSearch,
                                                     @RequestParam(required = false) String sortProperty,
                                                     @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        try {
            PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
            return checkNotNull(lwM2MModelsRepository.findDeviceLwm2mObjects(getTenantId(), pageLink));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/lwm2m/deviceProfile/bootstrap/{securityMode}/{bootstrapServerIs}", method = RequestMethod.GET)
    @ResponseBody
    public ServerSecurityConfig getLwm2mBootstrapSecurityInfo(@PathVariable("securityMode") String securityMode,
                                                              @PathVariable("bootstrapServerIs") boolean bootstrapServerIs) throws ThingsboardException {
        try {
            return lwM2MModelsRepository.getBootstrapSecurityInfo(securityMode, bootstrapServerIs);
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
