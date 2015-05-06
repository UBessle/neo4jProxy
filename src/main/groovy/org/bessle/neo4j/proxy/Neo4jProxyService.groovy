package org.bessle.neo4j.proxy

import groovyx.net.http.RESTClient
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class Neo4jProxyService {
	// inject GrailsApplication to obtain configuration properties for RESTClient endpoint
	// GrailsApplication application

	def client = new RESTClient( 'http://localhost:7474', 'application/json' )

	def getCypherResult(String requestCypher) {
		def response = client.post(path : '/db/data/cypher', body : requestCypher)
		return response
	}

    @Cacheable(value="neo4j", condition="#requestCypher.contains('match')", unless="#result.status!=200")
    def getCypherResult2(String requestCypher, HttpHeaders clientRequestHeaders) {
        Map backendRequestHeaders = [:]
        backendRequestHeaders['accept'] = "application/json; charset=UTF-8"
        clientRequestHeaders.each() { String headerName, def headerValues ->
            println "client request header - ${headerName} : ${headerValues}"
            if (headerName in ["user-agent","accept-language", "accept-encoding"] ) {
                //backendRequestHeaders[headerName] = headerValues[0]
                println "add backend request header - ${headerName} : ${headerValues[0]}"
            }
        }
        println "client.post(path: '/db/data/cypher', body: ${requestCypher}, headers: ${backendRequestHeaders})"
        def response = client.post(path: '/db/data/cypher', body: requestCypher, headers: backendRequestHeaders)
        println "response.data:${response.data}"
        println "response.status:${response.status}"
        println "condition:${requestCypher.contains('match')}"
        return response
    }
}

/* 
// Load JSON from File
def params = new JsonSlurper().parseText(new File("talks.json").text)
 
// send Cypher statement to Neo4j Server
def resp = client.post( path : '/db/data/transaction/commit', body : [statements:[[statement:query,parameters:[data:params]]]] )
 
assert resp.status == 200
assert resp.data.errors.isEmpty()
def talks = resp.data.results[0].data[0].row[0]
assert talks > 0
println "Inserted "+talks+" talks."
 
// println resp.data
*/