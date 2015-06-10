package org.bessle.neo4j.proxy

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.MethodNotSupportedException
import org.bessle.neo4j.proxy.util.HttpUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

@Slf4j
@Service
class Neo4jProxyService {
    @Autowired
    HttpUtil httpUtil

    @Autowired
    RESTClient neo4jClient

    @Cacheable(value="neo4j", key="#requestCypher.hashCode()"/*, condition="#requestCypher.contains('match')"*/, unless="#result.status!=200")
    HttpResponse postCypher(String requestCypher, HttpHeaders clientRequestHeaders, RequestMethod clientRequestMethod) {
        log.debug("postCypher(requestCypher=${requestCypher} type=${requestCypher.getClass().getName()}, clientRequestHeaders=${clientRequestHeaders})")
        Map backendRequestHeaders = httpUtil.copyRequestHeaders(
                clientRequestHeaders,
                ["content-length", "host"],
                ["accept" : "application/json; charset=UTF-8"]
        )

        log.trace("neo4jClient.post(path: '/db/data/cypher', body: ${requestCypher}, headers: ${backendRequestHeaders})")
        try {
            HttpResponse response
            switch (clientRequestMethod) {
                case RequestMethod.OPTIONS:
                    response = neo4jClient.options(
                            path: '/db/data/cypher',
                            body: requestCypher,
                            headers: backendRequestHeaders
                    )
                    break
                case RequestMethod.POST:
                    response = neo4jClient.post(
                            path: '/db/data/cypher',
                            body: requestCypher,
                            headers: backendRequestHeaders
                    )
                    break
                default:
                    throw new MethodNotSupportedException("method ${clientRequestMethod} is not supported by neo4jProxy")
            }

            log.debug("postCypher(): response=${response}")
            return response
        } catch (HttpResponseException hrex) {
            log.warn("neo4jClient.post did not succeed, caught HttpResponseException", hrex)
            return hrex.response
        }
    }
}
