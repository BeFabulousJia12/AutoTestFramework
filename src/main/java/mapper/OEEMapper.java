package mapper;

import model.OEEInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author You Jia
 * @Date 1/12/2018 12:53 PM
 */
@Mapper
public interface OEEMapper {

    List<OEEInfo> getOEEInfo(@Param("begindate")String beginDate,@Param("enddate")String endDate);

}
