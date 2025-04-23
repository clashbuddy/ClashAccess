package studio.clashbuddy.clashaccess.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;

@Configuration
@ComponentScan("studio.clashbuddy.clashaccess")
@EnableConfigurationProperties({ClashBuddySecurityClashAccessAppProperties.class, ClashBuddyClashAccessProperties.class})
public class ClashAccessAutoConfiguration {

}
