package studio.clashbuddy.clashaccess.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import studio.clashbuddy.clashaccess.properties.ClashBuddySecurityClashAccessAppProperties;


@Configuration
@ComponentScan("studio.clashbuddy.clashaccess")
@EnableConfigurationProperties({ClashBuddySecurityClashAccessAppProperties.class})
public class ClashAccessAutoConfiguration {
    private final Logger logger = LoggerFactory.getLogger(ClashAccessAutoConfiguration.class);
    @Bean("clashAccessMessageSource")
    public MessageSource clashAccessMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:clashaccess/messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void StatUp() {
        logger.info("\n" +
                "🚀✨ ClashAccess "+VersionReader.getVersion()+" is fully initialized and ready to protect this microservices! ✨🚀\n" +
                "🔒 Authorization: Enabled via @RequireAccess\n" +
                "⚡ Rate Limiting: Powered by @RateLimit\n" +
                "📦 Mode: Standalone (no metadata sharing)\n" +
                "🛡️  Let's keep your services secure and performant.\n"
        );
    }
}
