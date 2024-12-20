import com.company.modules.demo.dao.OrderDao;
import com.company.modules.demo.dao.UserInfoDao;
import com.company.modules.demo.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


@SpringBootTest(classes = com.company.MainApplication.class)
public class UserInfoDaoTest {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private OrderDao orderDao;

    @Test
    public void testInsert() throws Exception {
        UserInfo userInfo = new UserInfo(null, "张三", new Date(), 1, 1L, "420200");
        userInfoDao.insert(userInfo);
        userInfo = new UserInfo(null, "张三", new Date(), 1, 1L, "420100");
        userInfoDao.insert(userInfo);
    }

}
