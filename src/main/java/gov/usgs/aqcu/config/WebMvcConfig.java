package gov.usgs.aqcu.config;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.lang.reflect.Field;
import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import springfox.documentation.spring.web.json.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingStrategy;

import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantSerializer;
import com.aquaticinformatics.aquarius.sdk.timeseries.serializers.InstantDeserializer;
import gov.usgs.aqcu.serializer.SwaggerGsonSerializer;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("/resources/", "/webjars/")
				.setCacheControl(
						CacheControl.maxAge(30L, TimeUnit.DAYS).cachePublic())
				.resourceChain(true)
				.addResolver(new WebJarsResourceResolver());
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter < ? >> converters) {
		FieldNamingStrategy LOWER_CASE_CAMEL_CASE = new FieldNamingStrategy() {  
			@Override
			public String translateName(Field f) {
				return f.getName().substring(0, 1).toLowerCase() + f.getName().substring(1);
			}
		};
	
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(Instant.class, new InstantSerializer())
			.registerTypeAdapter(Instant.class, new InstantDeserializer())
			.registerTypeAdapter(Json.class, new SwaggerGsonSerializer())
			.setFieldNamingStrategy(LOWER_CASE_CAMEL_CASE)
			.create();
	
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
		gsonHttpMessageConverter.setGson(gson);
        converters.add(gsonHttpMessageConverter);
    }
}
