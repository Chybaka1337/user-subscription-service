package ru.webrise.user_subscription_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.webrise.user_subscription_service.entity.Subscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByName(String name);

    Optional<Subscription> findByNameIgnoreCase(String name);

    @Query("""
    select s.name
    from Subscription s
    join s.users us
    group by s.id, s.name
    order by count(us.id) desc
    limit 3
    """)
    List<String> findTop3PopularSubscriptions();
}
