package com.jacob.openfga.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

/**
 * Wires up the singleton {@link OpenFgaClient} bean from externalised
 * configuration (see {@code application.yml}).
 *
 * <p>
 * Connection settings are bound from the {@code openfga.*} prefix so they can
 * be overridden per environment or via environment variables without code
 * changes.
 */
@Configuration
public class OpenFGAConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenFGAConfig.class);

    /**
     * Base URL of the OpenFGA server, e.g. {@code http://localhost:8080}.
     */
    @Value("${openfga.api-url}")
    private String apiUrl;

    /**
     * Identifier of the store this service operates against.
     */
    @Value("${openfga.store-id:}")
    private String storeId;

    /**
     * Optional authorization model id; when empty the store's latest model is
     * used.
     */
    @Value("${openfga.authorization-model-id:}")
    private String authorizationModelId;

    /**
     * Builds the OpenFGA client used by the service layer.
     *
     * @return a configured, ready-to-use {@link OpenFgaClient}
     * @throws FgaInvalidParameterException if the supplied configuration is
     * invalid
     */
    @Bean
    public OpenFgaClient openFgaClient() throws FgaInvalidParameterException {
        log.info("Initialising OpenFGA client -> apiUrl={}, storeId={}, modelId={}",
                apiUrl,
                StringUtils.hasText(storeId) ? storeId : "<none>",
                StringUtils.hasText(authorizationModelId) ? authorizationModelId : "<latest>");

        ClientConfiguration configuration = new ClientConfiguration().apiUrl(apiUrl);

        // Store id and model id are optional at startup so the app can boot before
        // a store has been provisioned; they are only required for data operations.
        if (StringUtils.hasText(storeId)) {
            configuration.storeId(storeId);
        }
        if (StringUtils.hasText(authorizationModelId)) {
            configuration.authorizationModelId(authorizationModelId);
        }

        return new OpenFgaClient(configuration);
    }
}
