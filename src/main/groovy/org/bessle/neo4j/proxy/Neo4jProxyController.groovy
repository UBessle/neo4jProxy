package org.bessle.neo4j.proxy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/db/data")
class Neo4jProxyController {
	static responseFormats = ['json']

    @Autowired
    Neo4jProxyService neo4jProxyService

    @RequestMapping(value = "/cypher", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    String cypher(@RequestBody String requestCypher) {
            println "Neo4jProxyController.cypher()"
            println "requestCypher: ${requestCypher}"
        def response = neo4jProxyService.getCypherResult(requestCypher)
        println "response.status: ${response.status}"
        println "response.data: ${response.data}"
        println "response.headers: "
        response.headers.each() {
            println "    ${it.toString()}"
        }
        return response.data
        /*
        HttpHeaders headers = new HttpHeaders()
        return new ResponseEntity<String>(result, headers, HttpStatus.OK)
        */
    }

    @RequestMapping(value = "/cypher2", method = RequestMethod.POST)
    ResponseEntity<String> cypher2(@RequestBody String requestCypher) {
            println "Neo4jProxyController.cypher2()"
            println "requestCypher: ${requestCypher}"
        def response = neo4jProxyService.getCypherResult(requestCypher)
            println "response.status: ${response.status}"
            println "response.data: ${response.data}"
            println "response.headers: "

        HttpHeaders headers = new HttpHeaders()
        response.headers.each() {
            println "    ${it.toString()}"
            headers.add(it.name, it.value)
        }

        return new ResponseEntity<String>(response.data.toString(), headers, HttpStatus.valueOf(response.status))

        /*
        HttpHeaders headers = new HttpHeaders()
        return new ResponseEntity<String>(result, headers, HttpStatus.OK)
        */
    }


}
