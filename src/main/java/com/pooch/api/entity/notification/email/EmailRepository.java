package com.pooch.api.entity.notification.email;

import org.springframework.data.jpa.repository.JpaRepository;

interface EmailRepository extends JpaRepository<Email, Long> {

}
