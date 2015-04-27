@Grab("hibernate")
@Grab("h2")
import grails.persistence.Entity

@Entity
@Resource(uri="/db/data")
class Neo4jProxy {
    String name
}
