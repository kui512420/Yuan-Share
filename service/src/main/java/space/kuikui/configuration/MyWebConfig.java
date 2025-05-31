package space.kuikui.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import space.kuikui.interceptor.Pre;

import javax.servlet.http.HttpServletRequest;
/*

    @拦截器处理

 */

@Configuration
public class MyWebConfig implements WebMvcConfigurer {

         @Bean
        public Pre preInterceptor() {
             return new Pre();
         }
         @Override
         public void addInterceptors(InterceptorRegistry registry) {
              registry.addInterceptor(preInterceptor());
          }


}
