/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bessle.neo4j.proxy

import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

//@Configuration
//@EnableAutoConfiguration
@ComponentScan("org.bessle.neo4j.proxy")
@RestController
@SpringBootApplication
@EnableCaching
@Slf4j
class Application {
    @Value('${neo4j.server.url}')
    String neo4jServerUrl

	@RequestMapping("/")
	def helloWorld() {
		[message: "Hello World"]
	}

	static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args)
	}

	@Bean
	public RESTClient restClient() {
        log.info("Application: create restClient for ${neo4jServerUrl}")
		return new RESTClient( neo4jServerUrl, 'application/json' )
	}

    @Bean
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        return new ConcurrentMapCacheManager("neo4j");
    }

}
