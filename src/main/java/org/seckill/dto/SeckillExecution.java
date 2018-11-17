package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
//秒杀执行结果
public class SeckillExecution {
	
	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successKillId=" + successKillId + "]";
	}
	private long seckillId;
	//秒杀执行状态
	private int state;
	//状态表示
	private String stateInfo;
	//秒杀成功对象
	private SuccessKilled successKillId;
	
	
	
	public SeckillExecution(long seckillId,SeckillStateEnum stateEnum) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
	}
	
	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, SuccessKilled successKillId) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKillId = successKillId;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public SuccessKilled getSuccessKillId() {
		return successKillId;
	}
	public void setSuccessKilled(SuccessKilled successKillId) {
		this.successKillId = successKillId;
	}
	
	
	
}
