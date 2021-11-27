package com.mycompany.myapp.repository;

import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.JobHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the JobHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobHistoryRepository extends JHipsterCouchbaseRepository<JobHistory, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", (SELECT `job`.*, meta(`job`).id)[0] as `job`" +
        ", (SELECT `department`.*, meta(`department`).id)[0] as `department`" +
        ", (SELECT `employee`.*, meta(`employee`).id)[0] as `employee`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN =
        " LEFT JOIN #{#n1ql.bucket} `job` ON KEYS b.`job`" +
        " LEFT JOIN #{#n1ql.bucket} `department` ON KEYS b.`department`" +
        " LEFT JOIN #{#n1ql.bucket} `employee` ON KEYS b.`employee`";

    default Page<JobHistory> findAll(Pageable pageable) {
        return new PageImpl<>(findAllBy(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter} #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<JobHistory> findAllBy(String pageableStatement);

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<JobHistory> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    Optional<JobHistory> findById(String id);
}
