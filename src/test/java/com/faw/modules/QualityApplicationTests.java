package com.faw.modules;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.faw.modules.jobView.entity.ScheduleJobLogEntity;
import com.faw.modules.jobView.service.ScheduleJobLogService;
import com.faw.modules.outPutLog.service.TimeOutPutLogService;
import com.faw.utils.httpclient.HttpAPIService;
import com.faw.utils.httpclient.HttpResult;
import com.faw.utils.impala.ImpalaUtils;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QualityApplicationTests {
	@Autowired
	private TimeOutPutLogService timeOutPutLogService;
	@Autowired
	private HttpAPIService httpAPIService;
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;
	@Test
	public void dataSourceTest() {
		//数据源2测试
		//timeOutPutLogService.timeOutputLog();

		//默认数据源1
		ScheduleJobLogEntity s = scheduleJobLogService.selectOne(new EntityWrapper<ScheduleJobLogEntity>());

		System.out.println(new Gson().toJson(s));
	}
	@Test
	public void impalaTest(){
		//昨天
		String sql1 ="select CAR_MODELL, product_date, SUM(OUTPUT) as output from dcp.cfm_dcp_output_status_day where status0 = 'CP7' and factory = 'CP1' and product_date IN('2020-04-06','2020-04-07','2020-04-08','2020-04-09','2020-04-10','2020-04-11','2020-04-12') and doc_status = '1' GROUP BY CAR_MODELL, product_date order by CAR_MODELL";

		//      "select * from dcp.raw_dcp_fis_init_batch_car_data_statistics_hours_history where MODELL_NAME_1 = 'BORA DELIVAT' limit 1 ";

		String sql2  = "select product_hour, out_put as output ,factory,product_date from dcp.raw_dcp_fis_init_batch_car_data_statistics_hours_history where  product_date = '2020-04-16'";



		String sql3 =
				"select * from dcp.cfm_dcp_output_status_week  where MODELL_NAME_1 = 'BORA DELIVAT' limit 1 ";


		String sql5 = "select MAX(MONTH_),FACTORY  from dcp.cfm_dcp_output_status_month where year_ = '2020' and  doc_status = '1' and STATUS0 in('CP8','Z900','CP7','G700') GROUP BY FACTORY ";



		List<Map<String,Object>> dayList1 = ImpalaUtils.pageQueryForMap(sql1);
		List<Map<String,Object>> dayList2 = ImpalaUtils.pageQueryForMap(sql2);
		List<Map<String,Object>> dayList3 = ImpalaUtils.pageQueryForMap(sql3);


		System.out.println(
				"----1---"+ JSON.toJSONString(dayList1)
		);
		System.out.println(
				"----2---"+JSON.toJSONString(dayList2)
		);
		System.out.println(
				"----3---"+JSON.toJSONString(dayList3)
		);
	}

	@Test
	public void httpTest() throws  Exception {
		HttpResult str = httpAPIService.doPost("https://iaas-piweb.faw-vw.in:8080/dataServiceRest/parts");
		System.out.println("========="+str.getBody());
	}
}
