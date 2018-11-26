package con.ht.base.start.security;

import con.ht.base.start.security.config.FeignConfig;
import con.ht.base.start.security.config.SecurityConfig;
import con.ht.base.start.security.handler.FailLoginHandler;
import con.ht.base.start.security.handler.LogoutHandler;
import con.ht.base.start.security.handler.SuccessLoginHandler;
import con.ht.base.start.security.module.properties.UserCenterProperties;
import con.ht.base.start.security.service.UserDetailsServer;
import con.ht.base.start.security.utils.RedisTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author zhengyi
 * @date 11/19/18 11:29 AM
 **/
@EnableConfigurationProperties(UserCenterProperties.class)
@ConditionalOnProperty(value = "user-center.enable", havingValue = "true", matchIfMissing = true)
@Configuration
public class UserCenterAuthConfiguration {

    private final UserCenterProperties userCenterProperties;
    private final ServerProperties serverProperties;

    @Autowired
    public UserCenterAuthConfiguration(UserCenterProperties userCenterProperties, ServerProperties serverProperties) {
        this.userCenterProperties = userCenterProperties;
        this.serverProperties = serverProperties;
    }

    @Bean
    public RedisTokenUtils redisTokenUtils(StringRedisTemplate stringRedisTemplate) {
        return new RedisTokenUtils(stringRedisTemplate);
    }

    @Bean
    public LogoutHandler logoutHandler(FeignConfig feignConfig) {
        return new LogoutHandler(feignConfig);
    }

    @Bean
    public SuccessLoginHandler successLoginHandler() {
        return new SuccessLoginHandler();
    }

    @Bean
    public FailLoginHandler failLoginHandler() {
        return new FailLoginHandler();
    }

    @Bean
    public FeignConfig feignConfig() {
        return new FeignConfig(userCenterProperties);
    }

    @Bean
    public UserDetailsServer userDetailsServer(FeignConfig feignConfig, RedisTokenUtils redisTokenUtils) {
        return new UserDetailsServer(feignConfig.authService(), redisTokenUtils);
    }

    @Bean
    @ConditionalOnBean(FeignConfig.class)
    public SecurityConfig securityConfig(RedisTokenUtils redisTokenUtils, LogoutHandler logoutHandler, SuccessLoginHandler successLoginHandler, FailLoginHandler failLoginHandler, UserDetailsServer userDetailsServer) {
        return new SecurityConfig(userCenterProperties, logoutHandler, serverProperties, redisTokenUtils, successLoginHandler, failLoginHandler, userDetailsServer);
    }
}