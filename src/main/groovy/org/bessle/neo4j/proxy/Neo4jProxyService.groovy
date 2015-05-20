package org.bessle.neo4j.proxy

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.MethodNotSupportedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

@Slf4j
@Service
class Neo4jProxyService {
	@Autowired
    RESTClient neo4j

    @Autowired
    HttpUtil httpUtil

    @Cacheable(value="neo4j", key="#requestCypher.hashCode()"/*, condition="#requestCypher.contains('match')"*/, unless="#result.status!=200")
    HttpResponse getCypherResult(String requestCypher, HttpHeaders clientRequestHeaders, RequestMethod clientRequestMethod) {
        log.debug("getCypherResult(${requestCypher}, ${clientRequestHeaders})")
        Map backendRequestHeaders = httpUtil.copyRequestHeaders(
                clientRequestHeaders,
                ["content-length", "host"],
                ["accept" : "application/json; charset=UTF-8"]
        )

        log.trace("neo4j.post(path: '/db/data/cypher', body: ${requestCypher}, headers: ${backendRequestHeaders})")
        try {
            HttpResponse response
            switch (clientRequestMethod) {
                case RequestMethod.OPTIONS:
                    response = neo4j.options(
                            path: '/db/data/cypher',
                            body: requestCypher,
                            headers: backendRequestHeaders
                    )
                    break
                case RequestMethod.POST:
                    response = neo4j.post(
                            path: '/db/data/cypher',
                            body: requestCypher,
                            headers: backendRequestHeaders
                    )
                    break
                default:
                    throw new MethodNotSupportedException("method ${clientRequestMethod} is not supported by neo4jProxy")
            }

            log.debug("getCypherResult(): response=${response}")
            return response
        } catch (HttpResponseException hrex) {
            log.warn("neo4j.post did not succeed, caught HttpResponseException", hrex)
            return hrex.response
        }
    }
}
