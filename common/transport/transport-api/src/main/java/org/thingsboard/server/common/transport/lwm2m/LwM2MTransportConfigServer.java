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
package org.thingsboard.server.common.transport.lwm2m;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@Slf4j
@Component
@ConditionalOnExpression("('${service.type:null}'=='tb-transport' && '${transport.lwm2m.enabled:false}'=='true') || '${service.type:null}'=='monolith' || '${service.type:null}'=='tb-core'")
public class LwM2MTransportConfigServer {

    @Getter
    @Value("${transport.lwm2m.timeout:}")
    private Long timeout;

    @Getter
    @Value("${transport.sessions.report_timeout}")
    private long sessionReportTimeout;

    @Getter
    private String MODEL_PATH_DEFAULT = "models";

    @Getter
    private String KEY_STORE_DEFAULT_RESOURCE_PATH = "credentials";

    @Getter
    private String KEY_STORE_DEFAULT_FILE = "serverKeyStore.jks";

    @Getter
    private String APP_DIR = "common";

    @Getter
    private String TRANSPORT_DIR = "transport";

    @Getter
    private String LWM2M_DIR = "lwm2m";

    @Getter
    private String SRC_DIR = "src";

    @Getter
    private String MAIN_DIR = "main";

    @Getter
    private String RESOURCES_DIR = "resources";

    @Getter
    private String BASE_DIR_PATH = System.getProperty("user.dir");

    @Getter
    @Value("${transport.lwm2m.model_path_file:}")
    private String modelPathFile;

    @Getter
//    private String PATH_DATA_MICROSERVICE = "/usr/share/tb-lwm2m-transport/data$";
    private String PATH_DATA = "data";

    @Getter
    @Setter
    private List<ObjectModel> modelsValue;

    @Getter
    @Value("${transport.lwm2m.support_deprecated_ciphers_enable:}")
    private boolean supportDeprecatedCiphersEnable;

    @Getter
    @Value("${transport.lwm2m.secure.key_store_type:}")
    private String keyStoreType;

    @Getter
    @Value("${transport.lwm2m.secure.key_store_path_file:}")
    private String keyStorePathFile;

    @Getter
    @Setter
    private KeyStore keyStoreValue;

    @Getter
    @Value("${transport.lwm2m.secure.key_store_password:}")
    private String keyStorePasswordServer;

    @Getter
    @Value("${transport.lwm2m.secure.root_alias:}")
    private String rootAlias;

    @Getter
    @Value("${transport.lwm2m.secure.enable_gen_psk_rpk:}")
    private Boolean enableGenPskRpk;

    @Getter
    @Value("${transport.lwm2m.server.bind_address:}")
    private String serverHost;

    @Getter
    @Value("${transport.lwm2m.server.bind_port:}")
    private Integer serverPort;

    @Getter
    @Value("${transport.lwm2m.server.bind_port_cert:}")
    private Integer serverPortCert;

    @Getter
    @Value("${transport.lwm2m.server.secure.start_all:}")
    private boolean serverStartAll;

    @Getter
    @Value("${transport.lwm2m.server.secure.dtls_mode:}")
    private Integer serverDtlsMode;

    @Getter
    @Value("${transport.lwm2m.server.secure.bind_address:}")
    private String serverSecureHost;

    @Getter
    @Value("${transport.lwm2m.server.secure.bind_port:}")
    private Integer serverSecurePort;

    @Getter
    @Value("${transport.lwm2m.server.secure.bind_port_cert:}")
    private Integer serverSecurePortCert;

    @Getter
    @Value("${transport.lwm2m.server.secure.public_x:}")
    private String serverPublicX;

    @Getter
    @Value("${transport.lwm2m.server.secure.public_y:}")
    private String serverPublicY;

    @Getter
    @Value("${transport.lwm2m.server.secure.private_s:}")
    private String serverPrivateS;

    @Getter
    @Value("${transport.lwm2m.server.secure.alias:}")
    private String serverAlias;

    @Getter
    @Value("${transport.lwm2m.bootstrap.enable:}")
    private Boolean bootstrapEnable;

    @Getter
    @Value("${transport.lwm2m.secure.redis_url:}")
    private String redisUrl;

    @PostConstruct
    public void init() {
        modelsValue = ObjectLoader.loadDefault();
        File path = getPathModels();
        if (path.isDirectory()) {
            try {
                modelsValue.addAll(ObjectLoader.loadObjectsFromDir(path));
                log.info(" [{}] Models directory is a directory", path.getAbsoluteFile());
            } catch (Exception e) {
                log.error(" [{}] Could not parse the resource definition file", e.toString());
            }
        } else {
            log.error(" [{}] Read Models", path.getAbsoluteFile());
        }
        getInKeyStore();
    }

    private File getPathModels() {
        Path pathModels = (modelPathFile != null && !modelPathFile.isEmpty()) ? Paths.get(modelPathFile) :
                (new File(Paths.get(getBaseDirPath(), PATH_DATA, MODEL_PATH_DEFAULT).toUri()).isDirectory()) ?
                        Paths.get(getBaseDirPath(), PATH_DATA, MODEL_PATH_DEFAULT) :
                        Paths.get(getBaseDirPath(), APP_DIR, TRANSPORT_DIR, LWM2M_DIR, SRC_DIR, MAIN_DIR, RESOURCES_DIR, MODEL_PATH_DEFAULT);
        return (pathModels != null) ? new File(pathModels.toUri()) : null;
    }

    private KeyStore getInKeyStore() {
        try {
            if (keyStoreValue != null && keyStoreValue.size() > 0)
                return keyStoreValue;
        } catch (KeyStoreException e) {
            log.error("Uninitialized keystore [{}]", keyStoreValue.toString());
        }
        Path keyStorePath = (keyStorePathFile != null && !keyStorePathFile.isEmpty()) ? Paths.get(keyStorePathFile) :
                (new File(Paths.get(getBaseDirPath(), PATH_DATA, KEY_STORE_DEFAULT_RESOURCE_PATH, KEY_STORE_DEFAULT_FILE).toUri()).isFile()) ?
                        Paths.get(getBaseDirPath(), PATH_DATA, KEY_STORE_DEFAULT_RESOURCE_PATH, KEY_STORE_DEFAULT_FILE) :
                        Paths.get(getBaseDirPath(), APP_DIR, TRANSPORT_DIR, LWM2M_DIR, SRC_DIR, MAIN_DIR, RESOURCES_DIR, KEY_STORE_DEFAULT_RESOURCE_PATH, KEY_STORE_DEFAULT_FILE);
        File keyStoreFile = new File(keyStorePath.toUri());
        if (keyStoreFile.isFile()) {
            try {
                InputStream inKeyStore = new FileInputStream(keyStoreFile);
                keyStoreValue = KeyStore.getInstance(keyStoreType);
                keyStoreValue.load(inKeyStore, keyStorePasswordServer == null ? null : keyStorePasswordServer.toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
                log.error("[{}] Unable to load KeyStore  files server, folder is not a directory", e.getMessage());
                keyStoreValue = null;
            }
            log.info("[{}] Load KeyStore  files server, folder is a directory", keyStoreFile.getAbsoluteFile());
        } else {
            log.error("[{}] Unable to load KeyStore  files server, is not a file", keyStoreFile.getAbsoluteFile());
            keyStoreValue = null;
        }
        return keyStoreValue;
    }

    private String getBaseDirPath() {
        Path FULL_FILE_PATH;
        if (BASE_DIR_PATH.endsWith("bin")) {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH.replaceAll("bin$", ""));
        } else if (BASE_DIR_PATH.endsWith("conf")) {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH.replaceAll("conf$", ""));
        } else {
            FULL_FILE_PATH = Paths.get(BASE_DIR_PATH);
        }
        return FULL_FILE_PATH.toUri().getPath();
    }
}
