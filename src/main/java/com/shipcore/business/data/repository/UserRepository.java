package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select u
            from User u
            join fetch u.organization o
            where u.active = true
            order by u.lastName, u.firstName
            """)
    List<User> findAllActiveWithOrganization();

    @Query("""
            select u
            from User u
            join fetch u.organization o
            where u.id = :id
            """)
    Optional<User> findByIdWithOrganization(@Param("id") Long id);

    Optional<User> findFirstByOrganizationIdAndActiveTrueOrderByIdAsc(Long organizationId);

}
