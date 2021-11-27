package com.mycompany.myapp.repository;

import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Department entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartmentRepository extends JHipsterCouchbaseRepository<Department, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", (SELECT `location`.*, meta(`location`).id)[0] as `location`" +
        ", (SELECT `employee`.*, meta(`employee`).id FROM `employees` `employee`) as `employees`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN =
        " LEFT JOIN #{#n1ql.bucket} `location` ON KEYS b.`location`" + " LEFT NEST #{#n1ql.bucket} `employees` ON KEYS b.`employees`";

    default Page<Department> findAll(Pageable pageable) {
        return new PageImpl<>(findAllBy(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter} #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Department> findAllBy(String pageableStatement);

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Department> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    Optional<Department> findById(String id);
}
