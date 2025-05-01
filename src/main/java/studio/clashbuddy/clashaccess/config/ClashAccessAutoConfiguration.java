package studio.clashbuddy.clashaccess.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import studio.clashbuddy.clashaccess.properties.ClashBuddyClashAccessProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessGatewayProperties;
import studio.clashbuddy.clashaccess.properties.PushProperties;

@Configuration
@ComponentScan("studio.clashbuddy.clashaccess")
@EnableConfigurationProperties({ClashBuddySecurityClashAccessAppProperties.class, ClashBuddyClashAccessProperties.class, ClashBuddySecurityClashAccessGatewayProperties.class, PushProperties.class})
public class ClashAccessAutoConfiguration {

    @Bean("clashAccessMessageSource")
    public MessageSource clashAccessMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:clashaccess/messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
