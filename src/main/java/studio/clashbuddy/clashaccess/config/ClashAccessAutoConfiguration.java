package studio.clashbuddy.clashaccess.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.PushProperties;

@Configuration
@ComponentScan("studio.clashbuddy.clashaccess")
@EnableConfigurationProperties({ClashBuddySecurityClashAccessAppProperties.class, ClashBuddyClashAccessProperties.class, ClashBuddySecurityClashAccessGatewayProperties.class, PushProperties.class})
public class ClashAccessAutoConfiguration {

}
