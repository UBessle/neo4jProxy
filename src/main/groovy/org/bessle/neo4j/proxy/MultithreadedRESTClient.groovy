package org.bessle.neo4j.proxy

import groovyx.net.http.RESTClient
import org.apache.http.client.HttpClient
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.params.HttpParams
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.impl.client.DefaultHttpClient
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by BessleU on 20.05.2015.
 */
class MultithreadedRESTClient extends RESTClient {
    @Autowired
    ClientConnectionManager connManager

    /**
     * Constructor.
     * @see HTTPBuilder#HTTPBuilder()
     */
    public MultithreadedRESTClient() {
        super()
    }

    /**
     * See {@link HTTPBuilder#HTTPBuilder(Object)}
     * @param defaultURI default request URI (String, URI, URL or {@link URIBuilder})
     * @throws URISyntaxException
     */
    public MultithreadedRESTClient( Object defaultURI ) throws URISyntaxException {
        super( defaultURI )
    }

    /**
     * See {@link HTTPBuilder#HTTPBuilder(Object, Object)}
     * @param defaultURI default request URI (String, URI, URL or {@link URIBuilder})
     * @param defaultContentType default content-type (String or {@link ContentType})
     * @throws URISyntaxException
     */
    public MultithreadedRESTClient( Object defaultURI, Object defaultContentType ) throws URISyntaxException {
        super( defaultURI, defaultContentType )
    }



    protected HttpClient createClient( HttpParams params ) {

        return new DefaultHttpClient(connManager, params)
    }
}
