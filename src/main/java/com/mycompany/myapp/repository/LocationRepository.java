package com.mycompany.myapp.repository;

import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Location entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationRepository extends JHipsterCouchbaseRepository<Location, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", (SELECT `country`.*, meta(`country`).id)[0] as `country`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN = " LEFT JOIN #{#n1ql.bucket} `country` ON KEYS b.`country`";

    default Page<Location> findAll(Pageable pageable) {
        return new PageImpl<>(findAllBy(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter} #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Location> findAllBy(String pageableStatement);

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Location> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    Optional<Location> findById(String id);
}
