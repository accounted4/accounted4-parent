package com.accounted4.am.feature.finsec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

/**
 *
 * @author glenn
 */
public interface DebentureRepository extends Repository<DebentureEntity, Long> {

    Page<DebentureEntity> findAll(Pageable pageRequest);
    
}
