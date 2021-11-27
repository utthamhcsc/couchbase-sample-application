package com.mycompany.myapp.repository;

import static com.mycompany.myapp.repository.JHipsterCouchbaseRepository.pageableStatement;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.mycompany.myapp.domain.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JHipsterCouchbaseRepository<Employee, String> {
    String SELECT =
        "SELECT meta(b).id as __id, meta(b).cas as __cas, b.*" +
        ", (SELECT `job`.*, meta(`job`).id FROM `jobs` `job`) as `jobs`" +
        ", (SELECT `manager`.*, meta(`manager`).id)[0] as `manager`" +
        ", (SELECT `department`.*, meta(`department`).id)[0] as `department`" +
        " FROM #{#n1ql.bucket} b";

    String JOIN =
        " LEFT NEST #{#n1ql.bucket} `jobs` ON KEYS b.`jobs`" +
        " LEFT JOIN #{#n1ql.bucket} `manager` ON KEYS b.`manager`" +
        " LEFT JOIN #{#n1ql.bucket} `department` ON KEYS b.`department`";

    default Page<Employee> findAll(Pageable pageable) {
        return new PageImpl<>(findAllBy(pageableStatement(pageable, "b")), pageable, count());
    }

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter} #{[0]}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Employee> findAllBy(String pageableStatement);

    @Query(SELECT + JOIN + " WHERE b.#{#n1ql.filter}")
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    List<Employee> findAll();

    @Query(SELECT + " USE KEYS $1" + JOIN)
    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    Optional<Employee> findById(String id);
}
