package com.chinamobile.upload2obs.config;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: ObsConfig
 * @Description: 本类配置obs服务器的相关东西
 */
@Configuration
public class ObsConfig {
    /**
     * OBS地址
     */
    @Value("${obsServer.endPoint}")
    private String endPoint;
    /**
     * 验证ID
     */
    @Value("${obsServer.AccessKeyId}")
    private String AccessKeyId;
    /**
     * 验证密钥
     */
    @Value("${obsServer.SecretAccessKey}")
    private String SecretAccessKey;

    @Bean
    public ObsClient getObsClient(){
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        ObsClient obsClient = new ObsClient(AccessKeyId, SecretAccessKey, config);
        return obsClient;
    }

}
