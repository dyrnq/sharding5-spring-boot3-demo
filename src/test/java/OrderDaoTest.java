import com.company.common.utils.TimeUtils;
import com.company.modules.demo.dao.OrderDao;
import com.company.modules.demo.dao.UserInfoDao;
import com.company.modules.demo.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = com.company.MainApplication.class)
public class OrderDaoTest {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private OrderDao orderDao;


    @Test
    public void testInsert() throws Exception {
        String time1 = "2024-08-01";
        Order order1 = new Order(null, 1L, TimeUtils.string2Date(time1), 1L, "420200");
        orderDao.insert(order1);

        String time2 = "2024-07-01";
        Order order2 = new Order(null, 1L, TimeUtils.string2Date(time2), 1L, "420100");

        orderDao.insert(order2);
    }
}
