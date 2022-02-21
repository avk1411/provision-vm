package com.ripple.provisionvm.repositories;

import com.ripple.provisionvm.model.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    
   
    public List<AppUser> findByEmailId(String emailId);

    public List<AppUser> findByMobileNumber(String mobileNumber);

    @Query(value = "select * from app_user where email_id = :username or mobile_number=:username", nativeQuery = true)
    public Optional<AppUser> findByUsername(@Param("username")String username);
}
