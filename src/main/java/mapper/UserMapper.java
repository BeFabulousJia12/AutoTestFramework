package mapper;

import model.TestInfo;
import model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author You Jia
 * @Date 10/18/2017 11:26 AM
 */
@Mapper
public interface UserMapper {
    //根据具体的id查询user的信息
    List<UserInfo> getid(int id);
    //根据具体的name查询test的信息
    List<UserInfo> getTestInfo(@Param("address")String address, @Param("number")int number);
    //查询所有的消息
    List<UserInfo> getAll();

    //插入数据
    void insertUser(UserInfo userInfo);
}
