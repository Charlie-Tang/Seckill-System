package org.seckill.dao;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
	
	@Resource
	private SeckillDao SeckillDao;
	
	@Test
	public void testReduceNumber() {
		
		Date killTime = new Date();
		int updateCount = SeckillDao.reduceNumber(1000L, killTime);
		System.out.println(updateCount);
	}

	@Test
	public void testQueryById() {
		long id =1000;
		Seckill seckill = SeckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}

	@Test
	public void testQueryAll() {
		
		List<Seckill> seckills = SeckillDao.queryAll(0, 100);
		for(Seckill seckill:seckills)
		{
			System.out.println(seckill);
		}
	}

}
