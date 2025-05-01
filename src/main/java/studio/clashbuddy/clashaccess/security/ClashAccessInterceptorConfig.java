package studio.clashbuddy.clashaccess.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
class ClashAccessInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private ClashAccessControlInterceptor clashAccessControlInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clashAccessControlInterceptor)
                .addPathPatterns("/**"); // intercept ALL paths
    }
}
