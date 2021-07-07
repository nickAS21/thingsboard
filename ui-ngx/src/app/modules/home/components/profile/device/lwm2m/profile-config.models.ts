///
/// Copyright © 2016-2020 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { AbstractControl, ValidationErrors } from '@angular/forms';

export const OBSERVE_ATTR = 'observeAttr';
export const OBSERVE = 'observe';
export const ATTR = 'attribute';
export const TELEMETRY = 'telemetry';
export const KEY_NAME = 'keyName';
export const DEFAULT_ID_SERVER = 123;
export const DEFAULT_ID_BOOTSTRAP = 111;
export const DEFAULT_HOST_NAME = "localhost";
export const DEFAULT_PORT_SERVER_NO_SEC = 5685;
export const DEFAULT_PORT_SERVER_SEC = 5686;
export const DEFAULT_PORT_SERVER_SEC_CERT = 5688;
export const DEFAULT_PORT_BOOTSTRAP_NO_SEC = 5689;
export const DEFAULT_PORT_BOOTSTRAP_SEC = 5690;
export const DEFAULT_PORT_BOOTSTRAP_SEC_CERT = 5692;
export const DEFAULT_CLIENT_HOLD_OFF_TIME = 1;
export const DEFAULT_LIFE_TIME = 300;
export const DEFAULT_DEFAULT_MIN_PERIOD = 1;
export const DEFAULT_NOTIF_IF_DESIBLED = true;
export const DEFAULT_BINDING = "U";
export const DEFAULT_BOOTSTRAP_SERVER_ACCOUNT_TIME_OUT = 0;
export const LEN_MAX_PUBLIC_KEY_PSK = 182;
export const LEN_MAX_PUBLIC_KEY_RPK_X509 = 3000;
export const KEY_IDENT_REGEXP_PSK = /^[0-9a-fA-F]{64,64}$/;
export const KEY_PRIVATE_REGEXP = /^[0-9a-fA-F]{134,134}$/;
export const KEY_PUBLIC_REGEXP_PSK = /^[0-9a-fA-F]{182,182}$/;
export const KEY_PUBLIC_REGEXP_X509 = /^[0-9a-fA-F]{0,3000}$/;
export const CAMEL_CASE_REGEXP = /[-_&@.,*+!?^${}()|[\]\\]/g;
export const INSTANCES_ID_VALUE_MIN = 0;
export const INSTANCES_ID_VALUE_MAX = 65535;

//ok
export enum SECURITY_CONFIG_MODE {
  PSK = 'PSK',
  RPK = 'RPK',
  X509 = 'X509',
  NO_SEC = 'NO_SEC'
}
//ok
export const SECURITY_CONFIG_MODE_NAMES = new Map<SECURITY_CONFIG_MODE, string>(
  [
    [SECURITY_CONFIG_MODE.PSK, 'Pre-Shared Key'],
    [SECURITY_CONFIG_MODE.RPK, 'Raw Public Key'],
    [SECURITY_CONFIG_MODE.X509, 'X.509 Certificate'],
    [SECURITY_CONFIG_MODE.NO_SEC, 'No Security'],
  ]
);
//ok
export interface BootstrapServersSecurityConfig {
  shortId: number,
  lifetime: number,
  defaultMinPeriod: number,
  notifIfDisabled: boolean,
  binding: string
}

//ok
export interface ServerSecurityConfig {
  host?: string,
  port?: number,
  bootstrapServerIs?: boolean,
  securityMode: string,
  clientPublicKeyOrId?: string,
  clientSecretKey?: string,
  serverPublicKey?: string;
  clientHoldOffTime?: number,
  serverId?: number,
  bootstrapServerAccountTimeout: number
}

//ok
interface BootstrapSecurityConfig {
  servers: BootstrapServersSecurityConfig,
  bootstrapServer: ServerSecurityConfig,
  lwm2mServer: ServerSecurityConfig
}

//ok
export interface ProfileConfigModels {
  bootstrap: BootstrapSecurityConfig,
  observeAttr: {
    observe: string [],
    attribute: string [],
    telemetry: string [],
    keyName: []
  }
}

//ok
export function getDefaultBootstrapServersSecurityConfig(): BootstrapServersSecurityConfig {
  return {
    shortId: DEFAULT_ID_SERVER,
    lifetime: DEFAULT_LIFE_TIME,
    defaultMinPeriod: DEFAULT_DEFAULT_MIN_PERIOD,
    notifIfDisabled: DEFAULT_NOTIF_IF_DESIBLED,
    binding: DEFAULT_BINDING
  }
}

//ok
export function getDefaultBootstrapServerSecurityConfig(hostname: any): ServerSecurityConfig {
  return {
    host: hostname,
    port: getDefaultPortBootstrap(),
    bootstrapServerIs: true,
    securityMode: SECURITY_CONFIG_MODE.NO_SEC.toString(),
    serverPublicKey: '',
    clientHoldOffTime: DEFAULT_CLIENT_HOLD_OFF_TIME,
    serverId: DEFAULT_ID_BOOTSTRAP,
    bootstrapServerAccountTimeout: DEFAULT_BOOTSTRAP_SERVER_ACCOUNT_TIME_OUT
  }
}
//ok
export function getDefaultLwM2MServerSecurityConfig(hostname): ServerSecurityConfig {
  const DefaultLwM2MServerSecurityConfig = getDefaultBootstrapServerSecurityConfig(hostname);
  DefaultLwM2MServerSecurityConfig.bootstrapServerIs = false;
  DefaultLwM2MServerSecurityConfig.port = getDefaultPortServer();
  DefaultLwM2MServerSecurityConfig.serverId = DEFAULT_ID_SERVER;
  return DefaultLwM2MServerSecurityConfig;
}
//ok
export function getDefaultPortBootstrap(securityMode?: string): number {
  return (!securityMode || securityMode === SECURITY_CONFIG_MODE.NO_SEC.toString()) ? DEFAULT_PORT_BOOTSTRAP_NO_SEC :
    (securityMode === SECURITY_CONFIG_MODE.X509.toString()) ? DEFAULT_PORT_BOOTSTRAP_SEC_CERT : DEFAULT_PORT_BOOTSTRAP_SEC;
}
//ok
export function getDefaultPortServer(securityMode?: string): number {
  return (!securityMode || securityMode === SECURITY_CONFIG_MODE.NO_SEC.toString()) ? DEFAULT_PORT_SERVER_NO_SEC :
    (securityMode === SECURITY_CONFIG_MODE.X509.toString()) ? DEFAULT_PORT_SERVER_SEC_CERT : DEFAULT_PORT_SERVER_SEC;
}

//ok
function getDefaultProfileBootstrapSecurityConfig(hostname: any): BootstrapSecurityConfig {
  return {
    servers: getDefaultBootstrapServersSecurityConfig(),
    bootstrapServer: getDefaultBootstrapServerSecurityConfig(hostname),
    lwm2mServer: getDefaultLwM2MServerSecurityConfig(hostname)
  }
}

//ok
export function getDefaultProfileConfig(hostname?: any): ProfileConfigModels {
  return {
    bootstrap: getDefaultProfileBootstrapSecurityConfig((hostname)? hostname : DEFAULT_HOST_NAME),
    observeAttr: {
      observe: [],
      attribute: [],
      telemetry: [],
      keyName: []
    }
  };
}

//ok
export interface ResourceLwM2M {
  id: number,
  name: string,
  observe: boolean,
  attribute: boolean,
  telemetry: boolean,
  keyName: string
}
//ok
export interface Instance {
  id: number,
  resources: ResourceLwM2M[]
}

/**
 * multiple  == true  => Multiple
 * multiple  == false => Single
 * mandatory == true  => Mandatory
 * mandatory == false => Optional
 */
export interface ObjectLwM2M {
  id: number,
  name: string,
  multiple?: boolean,
  mandatory?: boolean,
  instances?: Instance []
}

export function getChangeInstancesIds (): ChangeInstancesIds {
  let changeInstancesIds: ChangeInstancesIds;
  changeInstancesIds.add = new Set<number>();
  changeInstancesIds.del = new Set<number>();
  return changeInstancesIds;

}

export interface ChangeInstancesIds {
  add: Set<number>,
  del: Set<number>
}

