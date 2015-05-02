package org.bessle.neo4j.proxy

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.ResponseBody
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
        println "requestCypher: ${requestCypher}"
        def result = neo4jProxyService.getCypherResult(requestCypher)
        println "result: ${result}"
    	/*
    	HttpHeaders headers = new HttpHeaders()
    	return new ResponseEntity<String>(result, headers, HttpStatus.OK)
        */
    }
}
