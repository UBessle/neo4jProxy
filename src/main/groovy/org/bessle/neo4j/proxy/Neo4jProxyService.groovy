package org.bessle.neo4j.proxy

import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Slf4j
@Service
class Neo4jProxyService {
	def client = new RESTClient( 'http://localhost:7474', 'application/json' )

    @Autowired
    HttpUtil httpUtil

    @Cacheable(value="neo4j", key="#requestCypher.hashCode()"/*, condition="#requestCypher.contains('match')"*/, unless="#result.status!=200")
    HttpResponse getCypherResult(String requestCypher, HttpHeaders clientRequestHeaders) {
        log.debug("getCypherResult(${requestCypher}, ${clientRequestHeaders})")
        Map backendRequestHeaders = httpUtil.copyRequestHeaders(
                clientRequestHeaders,
                ["content-length", "host"],
                ["accept" : "application/json; charset=UTF-8"]
        )

        log.trace("client.post(path: '/db/data/cypher', body: ${requestCypher}, headers: ${backendRequestHeaders})")
        HttpResponse response = client.post(
                path: '/db/data/cypher',
                body: requestCypher,
                headers: backendRequestHeaders
        )

        log.debug("getCypherResult(): response=${response}")
        return response
    }
}
