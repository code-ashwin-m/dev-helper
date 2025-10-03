package org.ashwin.example2;

import org.ashwin.service.annotations.Query;
import org.ashwin.service.annotations.Repository;
import org.ashwin.service.interfaces.BaseRepository;

import java.util.List;

@Repository(entity = Order.class)
public interface OrderRepository extends BaseRepository<Order> {
    List<Order> findByEmail(String email);

    @Query("SELECT * FROM orders WHERE email = ? AND product = ?")
    List<Order> findByEmailAndProduct(String mail, String mobile);
}
