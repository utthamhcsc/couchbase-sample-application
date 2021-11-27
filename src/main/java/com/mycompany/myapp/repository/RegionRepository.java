package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Region;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Couchbase repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends JHipsterCouchbaseRepository<Region, String> {}
