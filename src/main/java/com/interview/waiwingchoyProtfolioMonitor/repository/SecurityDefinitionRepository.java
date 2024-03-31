package com.interview.waiwingchoyProtfolioMonitor.repository;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecurityDefinitionRepository extends JpaRepository<SecurityDefinition, String> {
    @Query(value="select * from SECURITY_DEFINITION where SYMBOL = :val", nativeQuery = true)
    SecurityDefinition findBySymbol(@Param("val") String symbol);
}
