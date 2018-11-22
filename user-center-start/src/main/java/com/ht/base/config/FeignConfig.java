package com.ht.base.config;

import com.ht.base.module.properties.UserCenterProperties;
import com.ht.base.service.AuthServer;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @author zhengyi
 * @date 11/20/18 7:39 PM
 **/
public class FeignConfig {

    private UserCenterProperties userCenterProperties;

    public FeignConfig(UserCenterProperties userCenterProperties) {
        this.userCenterProperties = userCenterProperties;
    }

    public AuthServer authService() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(AuthServer.class, userCenterProperties.getUrl());
    }
}