package com.hsbc.demo.transaction_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.hsbc.demo.transaction_service.entity.*;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<AccountData, String>, 
                                        CrudRepository<AccountData, String>  {

    Optional<AccountData> findByUserId(String userId);

    List<AccountData> findAllByUserIdIn(Set<String> userIdList);

    Page<AccountData> findAllByStatusIn(Set<AccountStatus> statusList,Pageable pageable);
}
