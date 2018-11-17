package org.seckill.service;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
		"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	
	@Autowired
	private SeckillService SeckillService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void testGetSeckillList() {
		
		List<Seckill> list =SeckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetbyId() {
		
		long id = 1000L;
		Seckill seckill = SeckillService.getbyId(id);
		logger.info("seckill={}",seckill);
	}
	
	//exposed=true, md5=04f692dcd9de8068fef4fe7699b77232, seckillId=1008, now=0, start=0, end=0
	@Test
//	public void testExportSeckillUrl() {
//		long id =1008;
//		Exposer exposer = SeckillService.exportSeckillUrl(id);
//		logger.info("exposer={}",exposer);
//		
//	}
	public void testSeckillLogin() throws Exception
	{
		long id =1005;
		Exposer exposer = SeckillService.exportSeckillUrl(id);
		if (exposer.isExposed()) {
			logger.info("exposer={}",exposer);
			long userPhone = 46348239;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = SeckillService.executeSeckill(id, userPhone, md5);
				logger.info("result={}",execution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			}
			catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		}
		else
		{
			logger.warn("exposer={}",exposer);
		}
	}

	@Test
	public void testExecuteSeckill() throws Exception{
		long id =1008;
		long userPhone = 169422919;
		String md5 = "04f692dcd9de8068fef4fe7699b77232";
		try {
			SeckillExecution execution = SeckillService.executeSeckill(id, userPhone, md5);
			logger.info("result={}",execution);
		} catch (RepeatKillException e) {
			logger.error(e.getMessage());
		}
		catch (SeckillCloseException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Test
	public void executeSeckillProcedure()
	{
		long seckillId = 1005;
		long phone = 1368011101;
		Exposer exposer = SeckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			String md5 = exposer.getMd5();
			SeckillExecution seckillExecution =SeckillService.executeSeckillProcedure(seckillId, phone, md5);
			logger.info(seckillExecution.getStateInfo());
		}
	}
	
}
