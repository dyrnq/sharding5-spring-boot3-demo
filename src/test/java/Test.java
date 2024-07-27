import com.company.modules.demo.dao.OrderDao;
import com.company.modules.demo.dao.UserInfoDao;
import com.company.modules.demo.model.UserInfo;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.company.MainApplication.class)
public class Test {

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private OrderDao orderDao;

    @org.junit.Test
    public void test() throws Exception {
        UserInfo userInfo = new UserInfo(null, "张三", new Date(), 1, 1L, "420200");
        userInfoDao.insert(userInfo);
    }

}
